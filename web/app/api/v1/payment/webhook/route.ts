import { NextRequest, NextResponse } from "next/server";
import stripe from "../../../../../lib/stripe";
import {
  getTransactionBySessionId,
  updateTransactionStatus,
  updateTransactionWithPaymentIntent,
  updatePodderPaymentStatus,
} from "../../../../../utils/transactionUtils";
import { TransactionStatus } from "../../../../../types";
import { headers } from "next/headers";
import {
  ResponseCode,
  createSuccessResponse,
  createErrorResponse,
} from "../../../../../utils/apiResponse";
import logger from "../../../../../utils/logger";

// Helper function to convert ReadableStream to Buffer
async function streamToBuffer(
  stream: ReadableStream<Uint8Array>
): Promise<Buffer> {
  const reader = stream.getReader();
  const chunks: Uint8Array[] = [];

  while (true) {
    const { done, value } = await reader.read();
    if (done) break;
    chunks.push(value);
  }

  return Buffer.concat(chunks);
}

export async function POST(request: NextRequest) {
  try {
    // Get the raw request body as a buffer
    const rawBody = await streamToBuffer(
      request.body as ReadableStream<Uint8Array>
    );

    // Get the Stripe signature from headers
    const headersList = await headers();
    const signature = headersList.get("stripe-signature");

    if (!signature) {
      return NextResponse.json(
        createErrorResponse(
          ResponseCode.INVALID_WEBHOOK,
          "Missing Stripe signature"
        ),
        { status: 400 }
      );
    }

    // Verify the webhook signature
    let event;
    try {
      event = stripe.webhooks.constructEvent(
        rawBody,
        signature,
        process.env.STRIPE_WEBHOOK_SECRET as string
      );

      logger.debug(`Received webhook event: ${event.type}`, {
        eventId: event.id,
        eventType: event.type,
        apiVersion: event.api_version,
      });
    } catch (err: unknown) {
      const errorMessage = err instanceof Error ? err.message : "Unknown error";
      logger.error(
        `Webhook signature verification failed: ${errorMessage}`,
        err
      );
      return NextResponse.json(
        createErrorResponse(
          ResponseCode.INVALID_WEBHOOK,
          `Webhook signature verification failed: ${errorMessage}`
        ),
        { status: 400 }
      );
    }

    // Handle the event based on its type
    switch (event.type) {
      case "payment_intent.succeeded": {
        const paymentIntent = event.data.object;

        // Get the session ID from metadata if available
        const sessionId = paymentIntent.id;

        // If we have a session ID, use it to find the transaction
        if (sessionId) {
          const transaction = await getTransactionBySessionId(sessionId);

          if (!transaction) {
            logger.error(`No transaction found for session ID: ${sessionId}`);
            return NextResponse.json(
              createErrorResponse(
                ResponseCode.TRANSACTION_NOT_FOUND,
                `No transaction found for session ID: ${sessionId}`
              ),
              { status: 404 }
            );
          }

          // Update the transaction with payment intent ID and status
          await updateTransactionWithPaymentIntent(
            transaction.id as string,
            paymentIntent.id,
            TransactionStatus.COMPLETED
          );

          // Update the podder's payment status
          await updatePodderPaymentStatus(
            transaction.podId,
            transaction.userId
          );

          logger.success(
            `Payment completed for transaction: ${transaction.id}`,
            {
              podId: transaction.podId,
              userId: transaction.userId,
              sessionId,
              paymentIntentId: paymentIntent.id,
            }
          );
        } else {
          // If no session ID is available in metadata, log this case
          logger.warning(
            `Payment intent succeeded but no session ID found in metadata`,
            {
              paymentIntentId: paymentIntent.id,
            }
          );
        }

        break;
      }

      case "payment_intent.payment_failed": {
        const paymentIntent = event.data.object;
        const sessionId = paymentIntent.id;

        if (sessionId) {
          const transaction = await getTransactionBySessionId(sessionId);

          if (transaction) {
            await updateTransactionStatus(
              transaction.id as string,
              TransactionStatus.FAILED
            );

            logger.warning(
              `Payment failed for transaction: ${transaction.id}`,
              {
                podId: transaction.podId,
                userId: transaction.userId,
                sessionId,
                paymentIntentId: paymentIntent.id,
              }
            );
          }
        }

        break;
      }

      // Keep the checkout.session.completed handler as a fallback
      case "checkout.session.completed": {
        const session = event.data.object;

        // Get the transaction from the database
        const transaction = await getTransactionBySessionId(session.id);

        if (!transaction) {
          logger.error(`No transaction found for session ID: ${session.id}`);
          return NextResponse.json(
            createErrorResponse(
              ResponseCode.TRANSACTION_NOT_FOUND,
              `No transaction found for session ID: ${session.id}`
            ),
            { status: 404 }
          );
        }

        // Update the transaction with payment intent ID and status
        await updateTransactionWithPaymentIntent(
          transaction.id as string,
          session.payment_intent as string,
          TransactionStatus.COMPLETED
        );

        // Update the podder's payment status
        await updatePodderPaymentStatus(transaction.podId, transaction.userId);

        logger.success(`Payment completed for transaction: ${transaction.id}`, {
          podId: transaction.podId,
          userId: transaction.userId,
          sessionId: session.id,
          paymentIntentId: session.payment_intent,
        });

        break;
      }

      case "charge.refunded": {
        const charge = event.data.object;
        const paymentIntentId = charge.payment_intent;

        // Find transactions with this payment intent ID and update them
        // This would require an additional utility function to find by payment intent ID
        // For now, we'll log this event for monitoring
        logger.info(`Refund processed for payment intent: ${paymentIntentId}`);

        break;
      }

      default:
        // Unexpected event type
        logger.warning(`Unhandled event type: ${event.type}`);
    }

    // Return a success response
    return NextResponse.json(
      createSuccessResponse({ received: true }, ResponseCode.SUCCESS)
    );
  } catch (error) {
    logger.error("Error processing webhook:", error);
    return NextResponse.json(
      createErrorResponse(
        ResponseCode.WEBHOOK_PROCESSING_FAILED,
        "Failed to process webhook"
      ),
      { status: 500 }
    );
  }
}
