[![CircleCI](https://circleci.com/gh/amfleming/skeleton.svg?style=svg)](https://circleci.com/gh/amfleming/skeleton)


A receipt tagging web application.  Add receipt information and your own tag classification to organize them.

The application uses uses the Google Vision API.  It allows you to use your camera (on your phone or laptop) to snap a photo of your receipt and then automatically pull the merchant name and total amount.

The code includes an API using Java and jooq for the database storage of the receipt information.

Deployed on Amazon Web Services, with practice using gradle and docker.
