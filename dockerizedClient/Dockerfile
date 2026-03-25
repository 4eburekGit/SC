FROM ubuntu:22.04 AS qtbuilder
RUN apt-get update && apt-get install -y \
    build-essential \
    cmake \
    git \
    clang \
    libgl1-mesa-dev \
    qtbase5-dev \
    libqt5gui5 \
    libqt5widgets5 \
    libqt5sql5-psql \
    libqt5test5 \
    libqt5network5 \
    libxcb-cursor0

WORKDIR /app
COPY . /app
# Build main package
RUN mkdir build && cd build && cmake .. && make

# Tests
RUN cd .. && mkdir test

COPY ./test/. /app/test
# Build tests
RUN cd test && mkdir build && cd build && cmake .. && make

# Runtime
FROM ubuntu:22.04 AS qtruntime
RUN apt-get update && apt-get install -y \
    libx11-xcb1 \
    libxcb-cursor0 \
    libxkbcommon-x11-0 \
    qtbase5-dev \
    libqt5gui5 \
    libqt5widgets5 \
    libqt5sql5-psql \
    libqt5test5 \
    libqt5network5

WORKDIR /app

COPY --from=qtbuilder /app/test/build/TestSCCLient ./

COPY --from=qtbuilder /app/build/SCCLIENT ./

COPY --from=qtbuilder /app/launch.sh ./

CMD ["./launch.sh"]
