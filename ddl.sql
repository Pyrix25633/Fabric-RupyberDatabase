CREATE TABLE IF NOT EXISTS Ranks (
    id INT,
    rank VARCHAR(32) NOT NULL,
    color INT NOT NULL,
    emergencyOperator BOOLEAN NOT NULL,
    PRIMARY KEY (id),
    UNIQUE (rank)
);

CREATE TABLE IF NOT EXISTS Statuses (
    id INTEGER PRIMARY KEY,
    status VARCHAR(16),
    color INT NOT NULL,
    UNIQUE(status)
);

INSERT INTO Statuses VALUES
    (1, 'Out of Service', 0xFFFF55),
    (2, 'Available', 0x55FF55),
    (3, 'On Patrol', 0x00AA00),
    (4, 'Busy', 0xFFAA00),
    (5, 'En Route', 0x55FFFF),
    (6, 'On Scene', 0x00AAAA),
    (7, 'Emergency', 0xAA0000);

CREATE TABLE IF NOT EXISTS Players (
    id INTEGER PRIMARY KEY,
    uuid CHAR(36) NOT NULL,
    username VARCHAR(16) NULL,
    online BOOLEAN NOT NULL DEFAULT TRUE,
    statusId INT NULL DEFAULT NULL,
    rankId INT NULL DEFAULT NULL,
    callsign VARCHAR(16) NULL DEFAULT NULL,
    callsignReserved BOOLEAN NOT NULL DEFAULT FALSE,
    password CHAR(8) NULL DEFAULT NULL,
    token CHAR(16) NULL DEFAULT NULL,
    settings VARCHAR(64) NOT NULL DEFAULT '{"compactMode":false,"condensedFont":false,"sharpMode":false}',
    UNIQUE (uuid),
    UNIQUE (username),
    UNIQUE (callsign),
    FOREIGN KEY (statusId) REFERENCES Statuses(id),
    FOREIGN KEY (rankId) REFERENCES Ranks(id)
);

CREATE TABLE IF NOT EXISTS StatusLogs (
    id INTEGER PRIMARY KEY,
    playerId INT NOT NULL,
    statusId INT NOT NULL,
    changedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (playerId) REFERENCES Players(id),
    FOREIGN KEY (statusId) REFERENCES Statuses(id)
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

CREATE TABLE IF NOT EXISTS Priorities (
    id INTEGER PRIMARY KEY,
    priority VARCHAR(8),
    color INT NOT NULL,
    UNIQUE(priority)
);

CREATE TABLE IF NOT EXISTS Recipients (
    id INTEGER PRIMARY KEY,
    recipients VARCHAR(16),
    color INT NOT NULL,
    UNIQUE(recipients)
);

INSERT INTO Priorities VALUES
    (1, 'Low', 0x55FF55),
    (2, 'Priority', 0xFFFF55),
    (3, 'Major', 0xFF5555);

INSERT INTO Recipients VALUES
    (1, 'Nearby', 0xFFFF55),
    (2, 'Available', 0x55FF55),
    (3, 'All', 0xFF5555);

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
    priorityId INT NOT NULL,
    responseCodeId INT NOT NULL,
    recipientsId INT NOT NULL,
    incidentTypeId INT NOT NULL,
    locationX INT NOT NULL,
    locationY INT NOT NULL,
    locationZ INT NOT NULL,
    description VARCHAR(128) NULL,
    createdAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    createdBy INT NOT NULL,
    closedAt DATETIME NULL DEFAULT NULL,
    closedBy INT NULL DEFAULT NULL,
    FOREIGN KEY (priorityId) REFERENCES Priorities(id),
    FOREIGN KEY (emergencyCallId) REFERENCES EmergencyCalls(id),
    FOREIGN KEY (recipientsId) REFERENCES Recipients(id),
    FOREIGN KEY (responseCodeId) REFERENCES ResponseCodes(id),
    FOREIGN KEY (incidentTypeId) REFERENCES IncidentTypes(id),
    FOREIGN KEY (createdBy) REFERENCES Players(id),
    FOREIGN KEY (closedBy) REFERENCES Players(id)
);

CREATE TABLE IF NOT EXISTS Roles (
    id INTEGER PRIMARY KEY,
    role VARCHAR(8),
    color INT NOT NULL,
    UNIQUE(role)
);

INSERT INTO Roles VALUES
    (1, 'Officer', 0x5555FF),
    (2, 'Suspect', 0xAA00AA),
    (3, 'Victim', 0x00AA00);

CREATE TABLE IF NOT EXISTS IncidentPlayers (
    incidentId INT NOT NULL,
    roleId INT NOT NULL,
    playerId INT NOT NULL,
    addedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    removedAt DATETIME NULL DEFAULT NULL,
    FOREIGN KEY (incidentId) REFERENCES Incidents(id),
    FOREIGN KEY (roleId) REFERENCES Roles(id),
    FOREIGN KEY (playerId) REFERENCES Players(id)
);