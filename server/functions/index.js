/**
 * Firebase Cloud Functions entry point for the Spire API.
 *
 * The Express app and all routes now live in app.js so they can be shared
 * between Firebase (this file) and AWS Lambda (lambda.js). This file simply
 * wraps the shared app with Firebase's onRequest trigger.
 *
 * See a full list of supported triggers at https://firebase.google.com/docs/functions
 */

const { onRequest } = require("firebase-functions/v2/https");
const app = require("./app");

exports.spire = onRequest({ cors: true }, app);
