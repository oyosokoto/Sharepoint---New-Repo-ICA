import { NextRequest, NextResponse } from "next/server";
import stripe from "../../../../../lib/stripe";
import { createTransaction } from "../../../../../utils/transactionUtils";
import { auth } from "../../../../../lib/firebase-admin";
import { db } from "../../../../../lib/firebase-admin";
import { PaymentPod } from "@/types";
import {
  ResponseCode,
  createSuccessResponse,
  createErrorResponse,
} from "../../../../../utils/apiResponse";
import logger from "../../../../../utils/logger";

export async function POST(request: NextRequest) {
  try {
    // Get the request body
    const body = await request.json();
    const { podId, amount } = body;

    logger.debug("Received create payment intent request", { podId, amount });

    // Validate required fields
    if (!podId || !amount) {
      logger.warning(
        "Missing required fields in create payment intent request",
        body
      );
      return NextResponse.json(
        createErrorResponse(
          ResponseCode.BAD_REQUEST,
          "Missing required fields: podId and amount are required"
        ),
        { status: 400 }
      );
    }

    // Get the user ID from the authorization header
    const authHeader = request.headers.get("authorization");
    if (!authHeader || !authHeader.startsWith("Bearer ")) {
      logger.warning("Missing or invalid authorization header", {
        header: authHeader ? authHeader.substring(0, 10) + "..." : null,
      });
      return NextResponse.json(
        createErrorResponse(
          ResponseCode.UNAUTHORIZED,
          "Unauthorized: Missing or invalid authorization header"
        ),
        { status: 401 }
      );
    }

    // Extract the token
    const token = authHeader.split("Bearer ")[1];

    // Verify the token and get the user
    let userId;
    try {
      const decodedToken = await auth.verifyIdToken(token);
      userId = decodedToken.uid;
    } catch (error) {
      logger.error("Error verifying token:", error);
      return NextResponse.json(
        createErrorResponse(
          ResponseCode.UNAUTHORIZED,
          "Unauthorized: Invalid token"
        ),
        { status: 401 }
      );
    }

    // Get the pod details to include in the payment metadata
    const podDoc = await db.collection("pods").doc(podId).get();
    if (!podDoc.exists) {
      logger.warning(`Pod not found with ID: ${podId}`);
      return NextResponse.json(
        createErrorResponse(ResponseCode.POD_NOT_FOUND, "Pod not found"),
        { status: 404 }
      );
    }

    const pod = { id: podDoc.id, ...podDoc.data() } as PaymentPod;

    // Create a Stripe Payment Intent
    const paymentIntent = await stripe.paymentIntents.create({
      amount: Math.round(amount * 100), // Convert to cents/pence
      currency: "gbp",
      description: `Payment for ${pod.businessName || "Pod"}`,
      metadata: {
        podId,
        userId,
        businessName: pod.businessName || "Pod",
      },
    });

    // Create a transaction record in Firestore
    const transactionId = await createTransaction(
      userId,
      podId,
      amount,
      paymentIntent.id
    );

    // Log successful payment intent creation
    logger.success(`Created Stripe payment intent for pod: ${podId}`, {
      userId,
      podId,
      transactionId,
      paymentIntentId: paymentIntent.id,
      amount,
    });

    // Return the client secret and payment intent ID
    return NextResponse.json(
      createSuccessResponse(
        {
          clientSecret: paymentIntent.client_secret,
          paymentIntentId: paymentIntent.id,
          processorReference: paymentIntent.id, // Adding processor reference as requested
          transactionId,
        },
        ResponseCode.CREATED
      )
    );
  } catch (error) {
    logger.error("Error creating payment intent:", error);
    return NextResponse.json(
      createErrorResponse(
        ResponseCode.PAYMENT_FAILED,
        "Failed to create payment intent"
      ),
      { status: 500 }
    );
  }
}
