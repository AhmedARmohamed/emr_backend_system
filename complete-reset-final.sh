#!/bin/bash

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}=== COMPLETE EMR DATABASE RESET ===${NC}"

# Stop all containers
echo -e "${YELLOW}1. Stopping all containers...${NC}"
docker-compose down --remove-orphans

# List current volumes
echo -e "${YELLOW}2. Current volumes before cleanup:${NC}"
docker volume ls

# Remove ALL volumes aggressively
echo -e "${YELLOW}3. Removing all Docker volumes...${NC}"
docker-compose down -v --remove-orphans
docker volume prune -f

# Remove any EMR-specific volumes that might still exist
echo -e "${YELLOW}4. Removing EMR-specific volumes...${NC}"
EMR_VOLUMES=$(docker volume ls -q | grep -E "(emr|postgres)" 2>/dev/null || true)
if [ ! -z "$EMR_VOLUMES" ]; then
  echo "Found EMR volumes: $EMR_VOLUMES"
  docker volume rm $EMR_VOLUMES 2>/dev/null || true
else
  echo "No EMR volumes found"
fi

# Stop and remove any remaining containers
echo -e "${YELLOW}5. Cleaning up containers...${NC}"
docker container prune -f

# Show remaining volumes
echo -e "${YELLOW}6. Remaining volumes after cleanup:${NC}"
docker volume ls

# Start fresh containers
echo -e "${YELLOW}7. Starting fresh containers...${NC}"
docker-compose up -d postgres keycloak

# Wait for PostgreSQL to be ready
echo -e "${YELLOW}8. Waiting for PostgreSQL to be ready...${NC}"
sleep 15

# Function to check if PostgreSQL is ready
check_postgres() {
  docker exec emr-postgres pg_isready -U emr_user -d emrdb >/dev/null 2>&1
  return $?
}

# Wait up to 60 seconds for PostgreSQL
counter=0
while ! check_postgres; do
  if [ $counter -eq 12 ]; then # 12 * 5 = 60 seconds
    echo -e "${RED}Error: PostgreSQL did not start within 60 seconds${NC}"
    exit 1
  fi
  echo -e "${YELLOW}Still waiting for PostgreSQL...${NC}"
  sleep 5
  counter=$((counter + 1))
done

# Verify database is completely empty
echo -e "${YELLOW}9. Verifying database is clean...${NC}"
table_count=$(docker exec emr-postgres psql -U emr_user -d emrdb -t -c "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'public' AND table_type = 'BASE TABLE';" 2>/dev/null | tr -d ' ' | tr -d '\n')

if [ "$table_count" = "0" ]; then
  echo -e "${GREEN}✅ Database is completely clean!${NC}"
else
  echo -e "${RED}❌ Warning: Database still has $table_count tables${NC}"
  echo -e "${YELLOW}Listing existing tables:${NC}"
  docker exec emr-postgres psql -U emr_user -d emrdb -c "\dt"
  echo -e "${YELLOW}Manually cleaning remaining tables...${NC}"
  docker exec emr-postgres psql -U emr_user -d emrdb -c "DROP SCHEMA public CASCADE; CREATE SCHEMA public;"
fi

# Final verification
final_table_count=$(docker exec emr-postgres psql -U emr_user -d emrdb -t -c "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'public' AND table_type = 'BASE TABLE';" 2>/dev/null | tr -d ' ' | tr -d '\n')

if [ "$final_table_count" = "0" ]; then
  echo -e "${GREEN}✅ SUCCESS: Database is completely clean and ready!${NC}"
  echo -e "${GREEN}✅ You can now start your Spring Boot application.${NC}"
  echo -e "${YELLOW}Next steps:${NC}"
  echo -e "  1. Update your application.properties (use Hibernate + data.sql approach)"
  echo -e "  2. Create data.sql file with only INSERT statements"
  echo -e "  3. Run: ./mvnw spring-boot:run"
else
  echo -e "${RED}❌ ERROR: Failed to clean database completely${NC}"
  exit 1
fi
