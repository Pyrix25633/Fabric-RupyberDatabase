CREATE TABLE IF NOT EXISTS Ranks (
    id INT,
    rank VARCHAR(32) NOT NULL,
    color INT NOT NULL,
    emergencyOperator BOOLEAN NOT NULL,
    PRIMARY KEY (id),
    UNIQUE (rank)
);

CREATE TABLE IF NOT EXISTS Players (
    id INTEGER PRIMARY KEY,
    uuid CHAR(36) NOT NULL,
    username VARCHAR(16) NULL,
    online BOOLEAN NOT NULL DEFAULT TRUE,
    status INT NULL DEFAULT NULL,
    rankId INT NULL DEFAULT NULL,
    callsign VARCHAR(16) NULL DEFAULT NULL,
    callsignReserved BOOLEAN NOT NULL DEFAULT FALSE,
    password CHAR(8) NULL DEFAULT NULL,
    token CHAR(16) NULL DEFAULT NULL,
    settings VARCHAR(64) NOT NULL DEFAULT '{"compactMode":false,"condensedFont":false,"sharpMode":false}',
    UNIQUE (uuid),
    UNIQUE (username),
    UNIQUE (callsign),
    FOREIGN KEY (rankId) REFERENCES Ranks(id)
);

CREATE TABLE IF NOT EXISTS EmergencyCallNumbers (
    day DATE,
    number INT NOT NULL,
    PRIMARY KEY (day)
);

CREATE TABLE IF NOT EXISTS EmergencyCalls (
    id INTEGER PRIMARY KEY,
    callNumber INT NOT NULL,
    locationX INT NOT NULL,
    locationY INT NOT NULL,
    locationZ INT NOT NULL,
    createdAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    callerId INT NOT NULL,
    responderId INT NOT NULL,
    closedAt DATETIME NULL DEFAULT NULL,
    description VARCHAR(256),
    FOREIGN KEY (callerId) REFERENCES Players(id)
);

CREATE TABLE IF NOT EXISTS IncidentNumbers (
    day DATE,
    number INT NOT NULL,
    PRIMARY KEY (day)
);

CREATE TABLE IF NOT EXISTS ResponseCodes (
    id INT,
    code VARCHAR(16) NOT NULL,
    color INT NOT NULL,
    description VARCHAR(64) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE (code)
);

CREATE TABLE IF NOT EXISTS IncidentTypes (
    id INT,
    code VARCHAR(8) NOT NULL,
    color INT NOT NULL,
    description VARCHAR(64) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE (code)
);

CREATE TABLE IF NOT EXISTS Incidents (
    id INTEGER PRIMARY KEY,
    incidentNumber INT NOT NULL,
    emergencyCallId INT NULL DEFAULT NULL,
    priority INT NOT NULL,
    responseCodeId INT NOT NULL,
    recipients INT NOT NULL,
    incidentTypeId INT NOT NULL,
    locationX INT NOT NULL,
    locationY INT NOT NULL,
    locationZ INT NOT NULL,
    description VARCHAR(128) NULL,
    createdAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    createdBy INT NOT NULL,
    closedAt DATETIME NULL DEFAULT NULL,
    closedBy INT NULL DEFAULT NULL,
    FOREIGN KEY (emergencyCallId) REFERENCES EmergencyCalls(id),
    FOREIGN KEY (responseCodeId) REFERENCES ResponseCodes(id),
    FOREIGN KEY (incidentTypeId) REFERENCES IncidentTypes(id),
    FOREIGN KEY (createdBy) REFERENCES Players(id),
    FOREIGN KEY (closedBy) REFERENCES Players(id)
);

CREATE TABLE IF NOT EXISTS IncidentPlayers (
    incidentId INT NOT NULL,
    role INT NOT NULL,
    playerId INT NOT NULL,
    addedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    removedAt DATETIME NULL DEFAULT NULL,
    FOREIGN KEY (incidentId) REFERENCES Incidents(id),
    FOREIGN KEY (playerId) REFERENCES Players(id)
);