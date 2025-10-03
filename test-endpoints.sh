#!/bin/bash

# Script para probar los endpoints de la aplicación Spring Boot + Kogito
# Este script debe ejecutarse después de que la aplicación esté ejecutándose

PORT=${1:-8080}
BASE_URL="http://localhost:$PORT"

echo "=== TESTING ENDPOINTS FOR PERSON EVALUATION APP ==="
echo "Base URL: $BASE_URL"
echo

# Función para verificar si la aplicación está ejecutándose
check_health() {
    echo "1. Verificando estado de salud..."
    curl -s -w "\nStatus: %{http_code}\n" "$BASE_URL/actuator/health" || echo "Health check failed"
    echo
}

# Función para probar los endpoints de personas
test_persons_endpoints() {
    echo "2. Probando endpoints de personas..."
    
    # Test 1: Adulto (25 años) - debe ser APPROVED
    echo "Test 1: Crear persona adulta (25 años)"
    curl -X POST "$BASE_URL/persons" \
         -H "Content-Type: application/json" \
         -d '{"name":"John Doe","age":25,"email":"john@example.com"}' \
         -w "\nStatus: %{http_code}\n" || echo "Test 1 failed"
    echo
    
    # Test 2: Menor (15 años) - debe ser DENIED
    echo "Test 2: Crear persona menor (15 años)"
    curl -X POST "$BASE_URL/persons" \
         -H "Content-Type: application/json" \
         -d '{"name":"Jane Smith","age":15,"email":"jane@example.com"}' \
         -w "\nStatus: %{http_code}\n" || echo "Test 2 failed"
    echo
    
    # Test 3: Caso límite (18 años) - debe ser APPROVED
    echo "Test 3: Crear persona en límite (18 años)"
    curl -X POST "$BASE_URL/persons" \
         -H "Content-Type: application/json" \
         -d '{"name":"Mike Wilson","age":18,"email":"mike@example.com"}' \
         -w "\nStatus: %{http_code}\n" || echo "Test 3 failed"
    echo
    
    # Test 4: Obtener todas las personas
    echo "Test 4: Obtener todas las personas"
    curl -s "$BASE_URL/persons" \
         -w "\nStatus: %{http_code}\n" || echo "Test 4 failed"
    echo
}

# Función para probar el endpoint DMN directo
test_dmn_endpoint() {
    echo "3. Probando endpoint DMN directo..."
    curl -X POST "$BASE_URL/persons/persons" \
         -H "Content-Type: application/json" \
         -d '{"Person": {"name":"Direct DMN Test","age":30}}' \
         -w "\nStatus: %{http_code}\n" || echo "DMN test failed"
    echo
}

# Ejecutar todas las pruebas
check_health
test_persons_endpoints
test_dmn_endpoint

echo "=== TESTS COMPLETED ==="