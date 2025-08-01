-- Insert Service Types first (no dependencies)
INSERT INTO service_types (code, name, description, category) VALUES
                                                                  ('LAB_CBC', 'Complete Blood Count', 'Basic blood work analysis', 'LAB'),
                                                                  ('LAB_LIPID', 'Lipid Panel', 'Cholesterol and triglyceride levels', 'LAB'),
                                                                  ('RAD_XRAY', 'X-Ray', 'Basic radiological imaging', 'RADIOLOGY'),
                                                                  ('RAD_CT', 'CT Scan', 'Computed tomography imaging', 'RADIOLOGY'),
                                                                  ('RAD_MRI', 'MRI', 'Magnetic resonance imaging', 'RADIOLOGY'),
                                                                  ('CONS_GENERAL', 'General Consultation', 'General medical consultation', 'CONSULTATION'),
                                                                  ('CONS_CARDIO', 'Cardiology Consultation', 'Heart specialist consultation', 'CONSULTATION'),
                                                                  ('PROC_BIOPSY', 'Tissue Biopsy', 'Tissue sample collection and analysis', 'PROCEDURE');

-- Insert Facilities
INSERT INTO facilities (code, name, address, city, state, zip_code, phone, email, active, created_at, updated_at) VALUES
                                                                                                                      ('MAIN', 'Main Hospital', '123 Main St', 'Springfield', 'IL', '62701', '+1-555-0101', 'admin@mainhospital.com', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                                      ('CLINIC', 'Family Clinic', '456 Oak Ave', 'Springfield', 'IL', '62702', '+1-555-0102', 'info@familyclinic.com', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                                      ('LAB', 'QuickLab Diagnostics', '789 Pine St', 'Springfield', 'IL', '62703', '+1-555-0103', 'contact@quicklab.com', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Link Facilities with their available Services (using the IDs that were auto-generated)
INSERT INTO facility_services (facility_id, service_type_id)
SELECT f.id, s.id
FROM facilities f, service_types s
WHERE f.code = 'MAIN' AND s.code IN ('LAB_CBC', 'LAB_LIPID', 'RAD_XRAY', 'RAD_CT', 'RAD_MRI', 'CONS_GENERAL', 'CONS_CARDIO', 'PROC_BIOPSY');

INSERT INTO facility_services (facility_id, service_type_id)
SELECT f.id, s.id
FROM facilities f, service_types s
WHERE f.code = 'CLINIC' AND s.code IN ('LAB_CBC', 'LAB_LIPID', 'CONS_GENERAL');

INSERT INTO facility_services (facility_id, service_type_id)
SELECT f.id, s.id
FROM facilities f, service_types s
WHERE f.code = 'LAB' AND s.code IN ('LAB_CBC', 'LAB_LIPID');

-- Insert sample Patients
INSERT INTO patients (mrn, first_name, last_name, gender, date_of_birth, email, phone, address, city, state, zip_code, insurance_provider, insurance_policy_number, insurance_group_number, facility_id, created_at, updated_at)
SELECT 'MAIN001000', 'John', 'Doe', 'MALE', '1985-03-15', 'john.doe@email.com', '+1-555-1001', '100 Residential St', 'Springfield', 'IL', '62701', 'Blue Cross', 'BC123456789', 'GRP001', f.id, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM facilities f WHERE f.code = 'MAIN';

INSERT INTO patients (mrn, first_name, last_name, gender, date_of_birth, email, phone, address, city, state, zip_code, insurance_provider, insurance_policy_number, insurance_group_number, facility_id, created_at, updated_at)
SELECT 'MAIN001001', 'Jane', 'Smith', 'FEMALE', '1990-07-22', 'jane.smith@email.com', '+1-555-1002', '200 Residential Ave', 'Springfield', 'IL', '62702', 'Aetna', 'AET987654321', 'GRP002', f.id, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM facilities f WHERE f.code = 'MAIN';

INSERT INTO patients (mrn, first_name, last_name, gender, date_of_birth, email, phone, address, city, state, zip_code, insurance_provider, insurance_policy_number, insurance_group_number, facility_id, created_at, updated_at)
SELECT 'CLINIC001000', 'Bob', 'Johnson', 'MALE', '1978-11-10', 'bob.johnson@email.com', '+1-555-1003', '300 Family Ln', 'Springfield', 'IL', '62703', 'United Healthcare', 'UHC456789123', 'GRP003', f.id, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM facilities f WHERE f.code = 'CLINIC';