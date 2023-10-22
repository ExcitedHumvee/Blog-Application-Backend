#!/bin/bash
echo "Please make sure jq is installed, the tables in DB should be empty"
echo "########################################################################################"
echo
echo
echo
echo
# Function to extract token from API response
get_token() {
    local response="$1"
    echo "$response" | jq -r '.user.token'
}

# Command 1: Register a new user
register_response=$(curl --location 'http://localhost:8080/api/users' \
--header 'Content-Type: application/json' \
--header 'X-Requested-With: XMLHttpRequest' \
--data-raw '{"user":{"email":"john.doe@email.com","password":"password","username":"John Doe"}}')

# Print the registration response
echo "Registration Response:"
echo "$register_response"
echo

# Extract token from the registration response
token=$(get_token "$register_response")

# Print the token from the registration (optional)
echo "Registration Token: $token"
echo

# Command 2: Login with the registered user
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

# Command 3: Get the current user using the saved token
current_user_response=$(curl --location 'http://localhost:8080/api/user' \
--header 'Content-Type: application/json' \
--header 'X-Requested-With: XMLHttpRequest' \
--header "Authorization: Token $token")

# Print the current user response
echo "Current User Response:"
echo "$current_user_response"

# Command 4: Update the user using the saved token
update_user_response=$(curl --location --request PUT 'http://localhost:8080/api/user' \
--header 'Content-Type: application/json' \
--header 'X-Requested-With: XMLHttpRequest' \
--header "Authorization: Token $token" \
--data-raw '{"user":{"email":"john.doe@email.com"}}')

# Print the update user response
echo "Update User Response:"
echo "$update_user_response"
echo

# Command 5: Register a new user and store the token in token2
register_response_2=$(curl --location 'http://localhost:8080/api/users' \
--header 'Content-Type: application/json' \
--header 'X-Requested-With: XMLHttpRequest' \
--data-raw '{"user":{"email":"sam.miguel@email.com", "password":"password", "username":"SamMiguel"}}')

# Extract token from the second registration response
token2=$(get_token "$register_response_2")

# Extract username from the second registration response
username2=$(echo "$register_response_2" | jq -r '.user.username')

# Print the token and username from the second registration (optional)
echo "Registration Token (2): $token2"
echo "Username (2): $username2"
echo

# Additional Command: Login as the second user and store the token in token2
login_response_2=$(curl --location 'http://localhost:8080/api/users/login' \
--header 'Content-Type: application/json' \
--header 'X-Requested-With: XMLHttpRequest' \
--data-raw '{"user":{"email":"sam.miguel@email.com", "password":"password"}}')

# Print the login response for the second user
echo "Login Response (2):"
echo "$login_response_2"
echo

# Extract token from the second login response
token2=$(get_token "$login_response_2")

# Extract username from the second login response
username2=$(echo "$login_response_2" | jq -r '.user.username')

# Print the token from the second login (optional)
echo "Login Token (2): $token2"
echo

# Command 6: Get the profile for the second user using the saved token of user1
profile_response=$(curl --location "http://localhost:8080/api/profiles/$username2" \
--header 'Content-Type: application/json' \
--header 'X-Requested-With: XMLHttpRequest' \
--header "Authorization: Token $token")

# Print the profile response for the second user
echo "Profile Response:"
echo "$profile_response"
echo

# Command 7: Follow the second user using the saved token
follow_response=$(curl --location "http://localhost:8080/api/profiles/$username2/follow" \
--header 'Content-Type: application/json' \
--header 'X-Requested-With: XMLHttpRequest' \
--header "Authorization: Token $token" \
--data '')

# Print the follow response for the second user
echo "Follow Response:"
echo "$follow_response"
echo

# Command 8: Unfollow the second user using the saved token
unfollow_response=$(curl --location --request DELETE "http://localhost:8080/api/profiles/$username2/follow" \
--header 'Content-Type: application/json' \
--header 'X-Requested-With: XMLHttpRequest' \
--header "Authorization: Token $token" \
--data '')

# Print the unfollow response for the second user
echo "Unfollow Response:"
echo "$unfollow_response"
echo

# Command 9: Create an article using the saved token
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
