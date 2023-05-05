# mJPEGfPlay

[[**Homepage**](https://ed7n.github.io/mjpegfplay)]

## Building

    $ javac -d release --release 8 --source-path src src/eden/mjpegfplay/Main.java && jar -c -f release/mJPEGfPlay.jar -e eden.mjpegfplay.Main -C release eden -C src res

## Formatting

    $ prettier --write '**/*.java'
