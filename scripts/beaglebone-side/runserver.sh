gst-launch-1.0 -v v4l2src device=/dev/video0 io-mode=dmabuf ! image/jpeg,width=1920,height=1080,framerate=30/1  ! jpegdec ! videoconvert !x264enc tune=zerolatency bitrate=3000 speed-preset=superfast ! rtph264pay ! udpsink host=127.0.0.1 port=5000

