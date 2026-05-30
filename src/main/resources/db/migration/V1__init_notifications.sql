CREATE TABLE notifications (
                               id UUID PRIMARY KEY,
                               user_id VARCHAR(255) NOT NULL,
                               type VARCHAR(20) NOT NULL,
                               subject VARCHAR(255) NOT NULL,
                               body TEXT NOT NULL,
                               status VARCHAR(20) NOT NULL,
                               created_at TIMESTAMP NOT NULL
);