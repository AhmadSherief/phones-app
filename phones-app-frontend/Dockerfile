# Stage 1: Compile and Build angular codebase

# Use official node image as the base image
FROM node:12.16.1 as build

# Set the working directory
WORKDIR /usr/local/app

# Copy package.json to app
COPY ./package.json /usr/local/app/

# Install all the dependencies
RUN npm install

# Add the source code to app
COPY ./ /usr/local/app/

# Generate the build of the application
RUN npm run build


# Stage 2: Serve app with nginx server

# Use official nginx image as the base image
FROM nginx:1.21.6-alpine

# Copy the build output to replace the default nginx contents.
COPY --from=build /usr/local/app/dist/phones-app-frontend /usr/share/nginx/html

# Expose port 80
EXPOSE 80