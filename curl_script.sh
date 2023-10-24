#!/bin/bash
echo "Please make sure jq is installed, the tables in DB should be empty"
echo "To run on Unix/macOS line spacing should be LF. For WSL it should be CRLF"
echo "########################################################################################"
echo
echo
echo
echo

# Check if jq is installed
if ! command -v jq &> /dev/null; then
    echo "jq is not installed. Please install jq and run the script again."
    exit 1
fi

# Function to extract token from API response
get_token() {
    local response="$1"
    echo "$response" | jq -r '.user.token'
}

# Function to print JSON response nicely
pretty_print_json() {
    local response="$1"
    echo "$response" | jq .
}

# Command 1: Register a new user
register_response=$(curl --location 'http://localhost:8080/api/users' \
--header 'Content-Type: application/json' \
--header 'X-Requested-With: XMLHttpRequest' \
--data-raw '{"user":{"email":"john.doe@email.com","password":"password","username":"John Doe"}}')

# Print the registration response
echo "Registration Response:"
pretty_print_json "$register_response"
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
pretty_print_json "$login_response"
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
pretty_print_json "$current_user_response"

# Command 4: Update the user using the saved token
update_user_response=$(curl --location --request PUT 'http://localhost:8080/api/user' \
--header 'Content-Type: application/json' \
--header 'X-Requested-With: XMLHttpRequest' \
--header "Authorization: Token $token" \
--data-raw '{"user":{"email":"john.doe@email.com"}}')

# Print the update user response
echo "Update User Response:"
pretty_print_json "$update_user_response"
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
pretty_print_json "$login_response_2"
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
pretty_print_json "$profile_response"
echo

# Command 7: Follow the second user using the saved token
follow_response=$(curl --location "http://localhost:8080/api/profiles/$username2/follow" \
--header 'Content-Type: application/json' \
--header 'X-Requested-With: XMLHttpRequest' \
--header "Authorization: Token $token" \
--data '')

# Print the follow response for the second user
echo "Follow Response:"
pretty_print_json "$follow_response"
echo

# Command 8: Unfollow the second user using the saved token
unfollow_response=$(curl --location --request DELETE "http://localhost:8080/api/profiles/$username2/follow" \
--header 'Content-Type: application/json' \
--header 'X-Requested-With: XMLHttpRequest' \
--header "Authorization: Token $token" \
--data '')

# Print the unfollow response for the second user
echo "Unfollow Response:"
pretty_print_json "$unfollow_response"
echo

# Command 8.1: Follow the second user using the saved token
follow_response=$(curl --location "http://localhost:8080/api/profiles/$username2/follow" \
--header 'Content-Type: application/json' \
--header 'X-Requested-With: XMLHttpRequest' \
--header "Authorization: Token $token" \
--data '')

# Print the follow response for the second user
echo "Follow Response:"
pretty_print_json "$follow_response"
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
pretty_print_json "$create_article_response"
echo

# Command 10: Update an article using the saved token
update_article_response=$(curl --location --request PUT 'http://localhost:8080/api/articles/how-to-train-your-dragon' \
--header 'Content-Type: application/json' \
--header 'X-Requested-With: XMLHttpRequest' \
--header "Authorization: Token $token" \
--data '{
    "article": {
        "body": "With two hands",
        "title": "How to train your dragon",
        "description": "Ever wonder how?"
    }
}')

# Print the update article response
echo "Update Article Response:"
pretty_print_json "$update_article_response"
echo

# Command 11: Delete an article using the saved token
delete_article_response=$(curl --location --request DELETE 'http://localhost:8080/api/articles/how-to-train-your-dragon' \
--header 'Content-Type: application/json' \
--header 'X-Requested-With: XMLHttpRequest' \
--header "Authorization: Token $token" \
--data '')

# Print the delete article response
echo "Delete Article Response:"
pretty_print_json "$delete_article_response"
echo

# Command 12: Create an article using the saved token
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
pretty_print_json "$create_article_response"
echo

# Command 12.1: Create an article using the saved token2
create_article_response=$(curl --location 'http://localhost:8080/api/articles' \
--header 'Content-Type: application/json' \
--header 'X-Requested-With: XMLHttpRequest' \
--header "Authorization: Token $token2" \
--data '{
    "article": {
        "title": "How to decide between a dog and a cat?",
        "description": "Which one?",
        "body": "Dogs over cats any day",
        "tagList": [
            "pets"
        ]
    }
}')

# Print the create article response
echo "Create Article Response:"
pretty_print_json "$create_article_response"
echo

# Command 13: Get a single article using the saved token
get_article_response=$(curl --location 'http://localhost:8080/api/articles/how-to-train-your-dragon' \
--header 'Content-Type: application/json' \
--header 'X-Requested-With: XMLHttpRequest' \
--header "Authorization: Token $token")

# Print the get article response
echo "Get Single Article Response:"
pretty_print_json "$get_article_response"
echo

# Command 14: GET all articles with specific parameters (extra author=John%20Doe&tag=dragons&)
get_all_articles_response=$(curl --location 'http://localhost:8080/api/articles?offset=0&limit=20' \
--header 'Content-Type: application/json' \
--header 'X-Requested-With: XMLHttpRequest' \
--header "Authorization: Token $token")

# Command 15: GET feed articles
feed_articles_response=$(curl --location 'http://localhost:8080/api/articles/feed?offset=0&limit=20' \
--header 'Content-Type: application/json' \
--header 'X-Requested-With: XMLHttpRequest' \
--header "Authorization: Token $token")

# Print the feed articles response
echo "Feed Articles Response:"
pretty_print_json "$feed_articles_response"
echo

# Command 16: POST favorite article
favorite_article_response=$(curl --location --request POST 'http://localhost:8080/api/articles/how-to-decide-between-a-dog-and-a-cat%3F/favorite' \
--header 'Content-Type: application/json' \
--header 'X-Requested-With: XMLHttpRequest' \
--header "Authorization: Token $token" \
--data '')
# Print the favorite article response
echo "Favorite Article Response:"
pretty_print_json "$favorite_article_response"
echo

# Command 17: DELETE unfavorite article
unfavorite_article_response=$(curl --location --request DELETE 'http://localhost:8080/api/articles/how-to-decide-between-a-dog-and-a-cat%3F/favorite' \
--header 'Content-Type: application/json' \
--header 'X-Requested-With: XMLHttpRequest' \
--header "Authorization: Token $token" \
--data '')
# Print the unfavorite article response
echo "Unfavorite Article Response:"
pretty_print_json "$unfavorite_article_response"
echo

