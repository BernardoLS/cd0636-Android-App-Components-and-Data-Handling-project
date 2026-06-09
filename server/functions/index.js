/**
 * Import function triggers from their respective submodules:
 *
 * const {onCall} = require("firebase-functions/v2/https");
 * const {onDocumentWritten} = require("firebase-functions/v2/firestore");
 *
 * See a full list of supported triggers at https://firebase.google.com/docs/functions
 */

const { onRequest } = require("firebase-functions/v2/https");
const logger = require("firebase-functions/logger");
const express = require("express");
const app = express();
const fs = require("fs");
const path = require("path");

// Create and deploy your first functions
// https://firebase.google.com/docs/functions/get-started

// Load data from db.json
const dbPath = path.join(__dirname, "db.json");
const db = JSON.parse(fs.readFileSync(dbPath, "utf8"));
const buildings = db.buildings;
const cities = db.cities;
const countries = db.countries;

// Create API router
const apiRouter = express.Router();

// Define your API routes
app.get("/", (req, res) => {
  logger.info("Hello logs!", { structuredData: true });
  res.send("Hello from Firebase!");
});


/**
 * GET /api/buildings
 * Fetches buildings with optional pagination support
 * Query parameters:
 * - page: Page number (starting from 1)
 * - limit: Number of items per page (default: 10)
 */
apiRouter.get("/buildings", (req, res) => {
  const page = req.query.page ? parseInt(req.query.page) : null;
  const limit = req.query.limit ? parseInt(req.query.limit) : 10;
  logger.info(`Using page and Limit: Query: ${req.query} Page: ${page}, Limt: ${limit}`, { structuredData: true });

  // Helper function to enrich building with city and country objects
  const enrichBuilding = (building) => {
    const city = cities.find((c) => c.name === building.city);
    const country = countries.find((c) => c.name === building.country);

    return {
      ...building,
      city: city || building.city,
      country: country || building.country,
    };
  };

  // If no page parameter, return all buildings (non-paginated)
  if (page === null) {
    const enrichedBuildings = buildings.map(enrichBuilding);
    return res.json({
      buildings: enrichedBuildings,
    });
  }

  // Validate pagination parameters
  if (page < 1 || limit < 1) {
    return res.status(400).json({
      error: "Invalid pagination parameters. Page and limit must be positive integers.",
    });
  }

  // Calculate pagination
  const totalItems = buildings.length;
  const totalPages = Math.ceil(totalItems / limit);
  const startIndex = (page - 1) * limit;
  const endIndex = startIndex + limit;

  // Check if page is out of range
  if (page > totalPages && totalItems > 0) {
    return res.status(404).json({
      error: `Page ${page} not found. Total pages: ${totalPages}`,
    });
  }

  // Get paginated data and enrich with city and country objects
  const paginatedBuildings = buildings.slice(startIndex, endIndex).map(enrichBuilding);

  // Build response with pagination metadata
  const response = {
    buildings: paginatedBuildings,
    pagination: {
      current_page: page,
      page_size: limit,
      total_items: totalItems,
      total_pages: totalPages,
      has_next: page < totalPages,
      has_previous: page > 1,
    },
  };

  res.json(response);
});

/**
 * GET /api/buildings/paginated
 * Fetches buildings with offset-based pagination
 * Query parameters:
 * - offset: Number of items to skip (default: 0)
 * - limit: Number of items to return (default: 10)
 *
 * Example: /api/buildings/paginated?offset=20&limit=10
 */
apiRouter.get("/buildings/paginated", (req, res) => {
  const offset = req.query.offset ? parseInt(req.query.offset) : 0;
  const limit = req.query.limit ? parseInt(req.query.limit) : 10;

  logger.info(`Buildings Paginated API - offset: ${offset}, limit: ${limit}`, { structuredData: true });

  // Helper function to enrich building with city and country objects
  const enrichBuilding = (building) => {
    const city = cities.find((c) => c.name === building.city);
    const country = countries.find((c) => c.name === building.country);

    return {
      ...building,
      city: city || building.city,
      country: country || building.country,
    };
  };

  const totalItems = buildings.length;

  // Validate parameters
  if (offset < 0) {
    return res.status(400).json({
      error: "Invalid offset parameter. Offset must be a non-negative integer.",
    });
  }

  if (limit < 1) {
    return res.status(400).json({
      error: "Invalid limit parameter. Limit must be a positive integer.",
    });
  }

  // Check if offset is out of range
  if (offset >= totalItems && totalItems > 0) {
    return res.status(404).json({
      error: `Offset ${offset} is out of range. Total items: ${totalItems}`,
    });
  }

  // Calculate pagination
  const endIndex = offset + limit;
  const paginatedBuildings = buildings.slice(offset, endIndex).map(enrichBuilding);

  // Build response with pagination metadata
  const response = {
    buildings: paginatedBuildings,
    pagination: {
      offset: offset,
      limit: limit,
      total_items: totalItems,
      has_next: endIndex < totalItems,
      has_previous: offset > 0,
      next_offset: endIndex < totalItems ? endIndex : null,
    },
  };

  res.json(response);
});

/**
 * GET /api/cities
 * Fetches cities with optional pagination support
 * Query parameters:
 * - page: Page number (starting from 1)
 * - limit: Number of items per page (default: 10)
 */
apiRouter.get("/cities", (req, res) => {
  const page = parseInt(req.query.page) || null;
  const limit = parseInt(req.query.limit) || 10;

  // If no page parameter, return all cities (non-paginated)
  if (!page) {
    return res.json({
      cities: cities,
    });
  }

  // Validate pagination parameters
  if (page < 1 || limit < 1) {
    return res.status(400).json({
      error: "Invalid pagination parameters. Page and limit must be positive integers.",
    });
  }

  // Calculate pagination
  const totalItems = cities.length;
  const totalPages = Math.ceil(totalItems / limit);
  const startIndex = (page - 1) * limit;
  const endIndex = startIndex + limit;

  // Check if page is out of range
  if (page > totalPages && totalItems > 0) {
    return res.status(404).json({
      error: `Page ${page} not found. Total pages: ${totalPages}`,
    });
  }

  // Get paginated data
  const paginatedCities = cities.slice(startIndex, endIndex);

  // Build response with pagination metadata
  const response = {
    cities: paginatedCities,
    pagination: {
      current_page: page,
      page_size: limit,
      total_items: totalItems,
      total_pages: totalPages,
      has_next: page < totalPages,
      has_previous: page > 1,
    },
  };

  res.json(response);
});

/**
 * GET /api/countries
 * Fetches countries with optional pagination support
 * Query parameters:
 * - page: Page number (starting from 1)
 * - limit: Number of items per page (default: 10)
 */
apiRouter.get("/countries", (req, res) => {
  const page = parseInt(req.query.page) || null;
  const limit = parseInt(req.query.limit) || 10;

  // If no page parameter, return all countries (non-paginated)
  if (!page) {
    return res.json({
      countries: countries,
    });
  }

  // Validate pagination parameters
  if (page < 1 || limit < 1) {
    return res.status(400).json({
      error: "Invalid pagination parameters. Page and limit must be positive integers.",
    });
  }

  // Calculate pagination
  const totalItems = countries.length;
  const totalPages = Math.ceil(totalItems / limit);
  const startIndex = (page - 1) * limit;
  const endIndex = startIndex + limit;

  // Check if page is out of range
  if (page > totalPages && totalItems > 0) {
    return res.status(404).json({
      error: `Page ${page} not found. Total pages: ${totalPages}`,
    });
  }

  // Get paginated data
  const paginatedCountries = countries.slice(startIndex, endIndex);

  // Build response with pagination metadata
  const response = {
    countries: paginatedCountries,
    pagination: {
      current_page: page,
      page_size: limit,
      total_items: totalItems,
      total_pages: totalPages,
      has_next: page < totalPages,
      has_previous: page > 1,
    },
  };

  res.json(response);
});

// Mount the API router at /api
app.use("/api", apiRouter);


exports.spire = onRequest({ cors: true }, app);
