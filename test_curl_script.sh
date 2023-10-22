#!/bin/bash
echo "This file should only be executed after curl_script.sh has been executed"

# Function to extract token from API response
get_token() {
    local response="$1"
    echo "$response" | jq -r '.user.token'
}

# Command 1: Login with the registered user
login_response=$(curl --location 'http://localhost:8080/api/users/login' \
--header 'Content-Type: application/json' \
--header 'X-Requested-With: XMLHttpRequest' \
--data-raw '{"user":{"email":"john.doe@email.com", "password":"password"}}')

# Print the login response
echo "Login Response:"
echo "$login_response"
echo

# Extract token from the login response
token=$(get_token "$login_response")

# Print the token from the login (optional)
echo "Login Token: $token"
echo

# Command 2: Create an article using the saved token
create_article_response=$(curl --location 'http://localhost:8080/api/articles' \
--header 'Content-Type: application/json' \
--header 'X-Requested-With: XMLHttpRequest' \
--header "Authorization: Token $token" \
--data '{
    "article": {
        "title": "How to train your dragon",
        "description": "Ever wonder how?",
        "body": "Very carefully.",
        "tagList": [
            "training",
            "dragons"
        ]
    }
}')

# Print the create article response
echo "Create Article Response:"
echo "$create_article_response"
echo