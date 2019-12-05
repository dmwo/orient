gst-launch-1.0 -e -v udpsrc port=5000 ! application/x-rtp, encoding-name=JPEG, payload=26 ! rtpjpegdepay ! queue !jpegdec ! fpsdisplaysink text-overlay=true sink="xvimagesink" 
