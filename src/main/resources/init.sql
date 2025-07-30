-- PostgreSQL compatible init.sql for EMR System
-- Note: Database 'emrdb' will be created by Docker environment variables

-- Create ENUM types first (PostgreSQL requires explicit enum type creation)
CREATE TYPE gender_enum AS ENUM ('MALE', 'FEMALE', 'OTHER');
CREATE TYPE service_category_enum AS ENUM ('LAB', 'RADIOLOGY', 'CONSULTATION', 'PROCEDURE', 'OTHER');
CREATE TYPE service_status_enum AS ENUM ('SCHEDULED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED');

-- Create extension for UUID generation (optional, useful for future)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Create tables
CREATE TABLE facilities (
                            id BIGSERIAL PRIMARY KEY,
                            code VARCHAR(50) UNIQUE NOT NULL,
                            name VARCHAR(255) NOT NULL,
                            address VARCHAR(255),
                            city VARCHAR(100),
                            state VARCHAR(50),
                            zip_code VARCHAR(20),
                            phone VARCHAR(20),
                            email VARCHAR(100),
                            active BOOLEAN DEFAULT TRUE,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create trigger function for updating timestamps
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
RETURN NEW;
END;
$$ language 'plpgsql';

-- Create trigger for facilities
CREATE TRIGGER update_facilities_updated_at
    BEFORE UPDATE ON facilities
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TABLE service_types (
                               id BIGSERIAL PRIMARY KEY,
                               code VARCHAR(50) UNIQUE NOT NULL,
                               name VARCHAR(255) NOT NULL,
                               description TEXT,
                               category service_category_enum NOT NULL
);

CREATE TABLE facility_services (
                                   facility_id BIGINT NOT NULL,
                                   service_type_id BIGINT NOT NULL,
                                   PRIMARY KEY (facility_id, service_type_id),
                                   FOREIGN KEY (facility_id) REFERENCES facilities(id) ON DELETE CASCADE,
                                   FOREIGN KEY (service_type_id) REFERENCES service_types(id) ON DELETE CASCADE
);

CREATE TABLE patients (
                          id BIGSERIAL PRIMARY KEY,
                          mrn VARCHAR(50) UNIQUE NOT NULL,
                          first_name VARCHAR(100) NOT NULL,
                          last_name VARCHAR(100) NOT NULL,
                          gender gender_enum NOT NULL,
                          date_of_birth DATE NOT NULL,
                          email VARCHAR(100),
                          phone VARCHAR(20),
                          address VARCHAR(255),
                          city VARCHAR(100),
                          state VARCHAR(50),
                          zip_code VARCHAR(20),
                          insurance_provider VARCHAR(100),
                          insurance_policy_number VARCHAR(100),
                          insurance_group_number VARCHAR(100),
                          facility_id BIGINT NOT NULL,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          FOREIGN KEY (facility_id) REFERENCES facilities(id) ON DELETE RESTRICT
);

-- Create indexes
CREATE INDEX idx_patients_mrn ON patients(mrn);
CREATE INDEX idx_patients_name ON patients(first_name, last_name);
CREATE INDEX idx_patients_facility ON patients(facility_id);

-- Create trigger for patients
CREATE TRIGGER update_patients_updated_at
    BEFORE UPDATE ON patients
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TABLE patient_services (
                                  id BIGSERIAL PRIMARY KEY,
                                  patient_id BIGINT NOT NULL,
                                  service_type_id BIGINT NOT NULL,
                                  facility_id BIGINT NOT NULL,
                                  status service_status_enum DEFAULT 'SCHEDULED',
                                  scheduled_date TIMESTAMP,
                                  completed_date TIMESTAMP,
                                  notes TEXT,
                                  provider_name VARCHAR(255),
                                  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                  FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,
                                  FOREIGN KEY (service_type_id) REFERENCES service_types(id) ON DELETE RESTRICT,
                                  FOREIGN KEY (facility_id) REFERENCES facilities(id) ON DELETE RESTRICT
);

-- Create indexes for patient_services
CREATE INDEX idx_patient_services_patient ON patient_services(patient_id);
CREATE INDEX idx_patient_services_facility ON patient_services(facility_id);
CREATE INDEX idx_patient_services_status ON patient_services(status);
CREATE INDEX idx_patient_services_scheduled_date ON patient_services(scheduled_date);

-- Insert initial data
-- Insert facilities
INSERT INTO facilities (code, name, address, city, state, zip_code, phone, email) VALUES
                                                                                      ('MAIN', 'Main Hospital', '123 Healthcare Ave', 'New York', 'NY', '10001', '212-555-0100', 'info@mainhospital.com'),
                                                                                      ('NORTH', 'North Clinic', '456 Medical Blvd', 'Brooklyn', 'NY', '11201', '718-555-0200', 'info@northclinic.com'),
                                                                                      ('WEST', 'West Medical Center', '789 Health St', 'Queens', 'NY', '11101', '347-555-0300', 'info@westmedical.com');

-- Insert service types
INSERT INTO service_types (code, name, description, category) VALUES
-- Lab services
('CBC', 'Complete Blood Count', 'Full blood cell count analysis', 'LAB'),
('LIPID', 'Lipid Panel', 'Cholesterol and triglycerides test', 'LAB'),
('GLUCOSE', 'Blood Glucose Test', 'Blood sugar level measurement', 'LAB'),
('THYROID', 'Thyroid Function Test', 'TSH, T3, T4 levels', 'LAB'),
('URINE', 'Urinalysis', 'Complete urine analysis', 'LAB'),

-- Radiology services
('XRAY', 'X-Ray', 'Standard X-ray imaging', 'RADIOLOGY'),
('CT', 'CT Scan', 'Computed tomography scan', 'RADIOLOGY'),
('MRI', 'MRI Scan', 'Magnetic resonance imaging', 'RADIOLOGY'),
('ULTRASOUND', 'Ultrasound', 'Ultrasound imaging', 'RADIOLOGY'),
('MAMMO', 'Mammography', 'Breast cancer screening', 'RADIOLOGY'),

-- Consultation services
('GP', 'General Practitioner', 'Primary care consultation', 'CONSULTATION'),
('CARDIO', 'Cardiology Consultation', 'Heart specialist consultation', 'CONSULTATION'),
('ORTHO', 'Orthopedic Consultation', 'Bone and joint specialist', 'CONSULTATION'),
('NEURO', 'Neurology Consultation', 'Nervous system specialist', 'CONSULTATION'),

-- Procedures
('ECG', 'Electrocardiogram', 'Heart rhythm test', 'PROCEDURE'),
('ECHO', 'Echocardiogram', 'Heart ultrasound', 'PROCEDURE'),
('BIOPSY', 'Biopsy', 'Tissue sample collection', 'PROCEDURE');

-- Link services to facilities (all facilities offer all services initially)
INSERT INTO facility_services (facility_id, service_type_id)
SELECT f.id, st.id FROM facilities f CROSS JOIN service_types st;

-- Insert sample patients
INSERT INTO patients (mrn, first_name, last_name, gender, date_of_birth, email, phone, address, city, state, zip_code, insurance_provider, insurance_policy_number, facility_id) VALUES
                                                                                                                                                                                     ('MAIN001000', 'John', 'Doe', 'MALE', '1980-05-15', 'john.doe@email.com', '212-555-1001', '123 Main St', 'New York', 'NY', '10001', 'Blue Cross', 'BC123456', 1),
                                                                                                                                                                                     ('MAIN001001', 'Jane', 'Smith', 'FEMALE', '1992-08-22', 'jane.smith@email.com', '212-555-1002', '456 Oak Ave', 'New York', 'NY', '10002', 'Aetna', 'AE789012', 1),
                                                                                                                                                                                     ('NORTH001000', 'Michael', 'Johnson', 'MALE', '1975-03-10', 'michael.j@email.com', '718-555-2001', '789 Elm St', 'Brooklyn', 'NY', '11201', 'United Healthcare', 'UH345678', 2);

-- Insert sample patient services
INSERT INTO patient_services (patient_id, service_type_id, facility_id, status, scheduled_date, notes, provider_name) VALUES
                                                                                                                          (1, 1, 1, 'SCHEDULED', '2025-07-30 09:00:00', 'Annual checkup', 'Dr. Wilson'),
                                                                                                                          (1, 6, 1, 'SCHEDULED', '2025-07-30 10:00:00', 'Chest X-ray for cough', 'Dr. Brown'),
                                                                                                                          (2, 2, 1, 'COMPLETED', '2025-07-28 14:00:00', 'Routine lipid check', 'Dr. Davis'),
                                                                                                                          (3, 11, 2, 'SCHEDULED', '2025-08-01 11:00:00', 'Follow-up consultation', 'Dr. Lee');

-- Create views for common queries (optional but useful)
CREATE VIEW patient_summary AS
SELECT
    p.id,
    p.mrn,
    p.first_name || ' ' || p.last_name AS full_name,
    p.gender,
    p.date_of_birth,
    p.email,
    p.phone,
    f.name AS facility_name,
    p.insurance_provider,
    p.created_at
FROM patients p
         JOIN facilities f ON p.facility_id = f.id;

CREATE VIEW service_summary AS
SELECT
    ps.id,
    p.mrn,
    p.first_name || ' ' || p.last_name AS patient_name,
    st.name AS service_name,
    st.category,
    f.name AS facility_name,
    ps.status,
    ps.scheduled_date,
    ps.completed_date,
    ps.provider_name
FROM patient_services ps
         JOIN patients p ON ps.patient_id = p.id
         JOIN service_types st ON ps.service_type_id = st.id
         JOIN facilities f ON ps.facility_id = f.id;

-- Add comments to tables for documentation
COMMENT ON TABLE facilities IS 'Healthcare facilities in the EMR system';
COMMENT ON TABLE service_types IS 'Types of medical services offered';
COMMENT ON TABLE patients IS 'Patient information and demographics';
COMMENT ON TABLE patient_services IS 'Services scheduled or completed for patients';

-- Grant permissions (adjust as needed for your security requirements)
-- GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO emr_user;
-- GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO emr_user;