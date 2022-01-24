PRAGMA foreign_keys = ON;

CREATE TABLE "crimes" (
	"id"               INTEGER PRIMARY KEY,
	"solved"           INTEGER NOT NULL,
	"title"            TEXT NOT NULL,
	"suspect"          TEXT,
	"description"      TEXT,
	"creation_date"    INTEGER NOT NULL,
	"image_uri"        TEXT
);

INSERT INTO "crimes" ("solved", "title", "suspect", "description", "creation_date")
VALUES 	(0, "Broken bottle", "Somebody", "Somebody crashed a bottle of vine.", 0),
        (0, "Broken bottle", "Sandy", "Somebody crashed a bottle of vine.", 0),
        (0, "Broken bottle", "Melisa", "Somebody crashed a bottle of vine.", 0);