-- EMR System Data Initialization
-- Only INSERT statements - Hibernate creates the tables

-- Insert facilities
INSERT INTO facilities (code, name, address, city, state, zip_code, phone, email, active) VALUES
                                                                                              ('MAIN', 'Main Hospital', '123 Healthcare Ave', 'New York', 'NY', '10001', '212-555-0100', 'info@mainhospital.com', true),
                                                                                              ('NORTH', 'North Clinic', '456 Medical Blvd', 'Brooklyn', 'NY', '11201', '718-555-0200', 'info@northclinic.com', true),
                                                                                              ('WEST', 'West Medical Center', '789 Health St', 'Queens', 'NY', '11101', '347-555-0300', 'info@westmedical.com', true);

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