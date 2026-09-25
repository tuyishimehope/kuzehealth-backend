# SMS API Test Script
# This script tests your Pindo SMS Service endpoints

$baseUrl = "http://localhost:8080/api/sms"

Write-Host "🚀 Testing Pindo SMS Service API" -ForegroundColor Green
Write-Host "=================================" -ForegroundColor Green

# Test 1: Send Single SMS
Write-Host "`n📱 Test 1: Send Single SMS" -ForegroundColor Yellow
$singleSmsData = @{
    to = "+250786125117"  # Replace with your test phone number
    text = "Hello from Pindo SMS Service! This is a test message."
    sender = "PindoTest"
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri "$baseUrl/send" -Method Post -Body $singleSmsData -ContentType "application/json"
    Write-Host "✅ Single SMS Response:" -ForegroundColor Green
    $response | ConvertTo-Json -Depth 3
} catch {
    Write-Host "❌ Single SMS Error: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.ErrorDetails) {
        Write-Host "Details: $($_.ErrorDetails.Message)" -ForegroundColor Red
    }
}

# Test 2: Send Bulk SMS
Write-Host "`n📧 Test 2: Send Bulk SMS" -ForegroundColor Yellow
$bulkSmsData = @{
    recipients = @(
        @{ phonenumber = "+250781356759"; name = "Test User 1" },
        @{ phonenumber = "+250700000000"; name = "Test User 2" }
    )
    text = "Bulk SMS test from Pindo Service!"
    sender = "PindoBulk"
} | ConvertTo-Json -Depth 3

try {
    $response = Invoke-RestMethod -Uri "$baseUrl/send-bulk" -Method Post -Body $bulkSmsData -ContentType "application/json"
    Write-Host "✅ Bulk SMS Response:" -ForegroundColor Green
    $response | ConvertTo-Json -Depth 3
} catch {
    Write-Host "❌ Bulk SMS Error: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.ErrorDetails) {
        Write-Host "Details: $($_.ErrorDetails.Message)" -ForegroundColor Red
    }
}

Write-Host "`n🎯 SMS API Testing Complete!" -ForegroundColor Green
Write-Host "Note: Make sure your Spring Boot application is running on port 8080" -ForegroundColor Cyan 