# BUILD: docker-build-kamehouse.sh : builds the docker image from this file
# RUN: docker-run-kamehouse.sh : runs a temporary container from an image built from this file

ARG DOCKER_IMAGE_BASE
FROM ${DOCKER_IMAGE_BASE}
LABEL maintainer="brest.nico@gmail.com"

# Disable interactions when building the image
ENV DEBIAN_FRONTEND=noninteractive

# Setup users 
ARG KAMEHOUSE_USERNAME=goku
ENV KAMEHOUSE_USERNAME=${KAMEHOUSE_USERNAME}
ARG KAMEHOUSE_PASSWORD=gohan
ENV KAMEHOUSE_PASSWORD=${KAMEHOUSE_PASSWORD}

# Copy docker setup-container folder
COPY --chown=${KAMEHOUSE_USERNAME}:users docker/setup-container /home/${KAMEHOUSE_USERNAME}/docker/setup-container
RUN chmod a+x /home/${KAMEHOUSE_USERNAME}/docker/setup-container/scripts/*

# Setup container base apps, user and folders
RUN /home/${KAMEHOUSE_USERNAME}/docker/setup-container/scripts/dockerfile-setup-container.sh -u ${KAMEHOUSE_USERNAME} -p ${KAMEHOUSE_PASSWORD}

# Copy docker setup-kamehouse folder
COPY --chown=${KAMEHOUSE_USERNAME}:users docker/setup-kamehouse /home/${KAMEHOUSE_USERNAME}/docker/setup-kamehouse
RUN chmod a+x /home/${KAMEHOUSE_USERNAME}/docker/setup-kamehouse/scripts/*

# Copy kamehouse git repo into container
RUN mkdir -p /home/${KAMEHOUSE_USERNAME}/git
RUN chown -R ${KAMEHOUSE_USERNAME}:users /home/${KAMEHOUSE_USERNAME}/git
COPY --chown=${KAMEHOUSE_USERNAME}:users . /home/${KAMEHOUSE_USERNAME}/git/kamehouse

# Run docker-build-kamehouse.sh with -b to skip docker cache from this point onwards
ARG BUILD_DATE_KAMEHOUSE=0000-00-00
RUN echo "${BUILD_DATE_KAMEHOUSE}" > /home/${KAMEHOUSE_USERNAME}/.docker-image-build-date; 

# Setup kamehouse in the container
ARG DOCKER_IMAGE_TAG
RUN /home/${KAMEHOUSE_USERNAME}/docker/setup-kamehouse/scripts/dockerfile-setup-kamehouse.sh -u ${KAMEHOUSE_USERNAME} -t ${DOCKER_IMAGE_TAG}

# Expose ports
EXPOSE 22 80 443 3306 5000 8000 8080 9090

# Set timezone
ENV TZ=Europe/Madrid
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

CMD ["/home/${KAMEHOUSE_USERNAME}/programs/kamehouse-shell/bin/kamehouse/docker/docker-container/docker-init-kamehouse.sh"]