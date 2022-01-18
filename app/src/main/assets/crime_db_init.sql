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

INSERT INTO "crimes" ("solved", "title", "creation_date")
VALUES 	(0, "DB initialized!", 0);