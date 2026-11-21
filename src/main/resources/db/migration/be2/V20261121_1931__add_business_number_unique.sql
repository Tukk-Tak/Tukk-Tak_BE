ALTER TABLE business_verification
    ADD CONSTRAINT uk_business_verification_business_number UNIQUE (business_number);