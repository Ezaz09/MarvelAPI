# Marvel API
This application works with Marvel API 

Work links:
- GET /v1/public/characters
- GET /v1/public/characters/{characterId}
- GET /v1/public/characters/{characterId}/comics
- GET /v1/public/comics
- GET /v1/public/comics/{comicId}
- GET /v1/public/comics/{comicId}/characters

And can store your own characters and comics

# Libraries
- JDK 1.8
- Lombok 1.18.10
- Spring Boot
- Liquibase
- MySQL

# How to run 
- Create a new schema in your DB with a name "marvel_api".
- Initialize next environment variables:
  - ${MySQL_DB}
  - ${MySQL_UN}
  - ${MySQL_PASS}
  - ${PublicApiKey}
  - ${PrivateApiKey}
  
