echo "Launching $BUILD_NAME IN AMAZON ECS"
ecs-cli configure profile --access-key $AWS_ACCESS_KEY --secret-key $AWS_SECRET_KEY
ecs-cli configure --region eu-west-3 --cluster aws-services-dev
ecs-cli compose --file scripts/docker/docker-compose.yml up
rm -rf ~/.ecs
