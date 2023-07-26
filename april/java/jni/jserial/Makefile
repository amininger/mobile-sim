include ../common.mk

LIB_APRIL_JSERIAL_OBJS = serial.o serial_jni.o ioutils.o

LIB=libjserial.so
DESTLIB=../../../lib/$(LIB)

ALL = $(DESTLIB)

#############################################################
# MacOS X
ifeq "$(shell uname)" "Darwin"

.phony:
	@echo "JSerial is not supported on Darwin/MacOS."

ALL = .phony

endif

#############################################################
all: $(ALL)


$(DESTLIB): $(LIB)
	@echo "    [$@]"
	cp $(LIB) $(DESTLIB)

libjserial.so: $(LIB_APRIL_JSERIAL_OBJS)
	$(LD) $(LDFLAGS) --shared $(LIB_APRIL_JSERIAL_OBJS) -o libjserial.so

clean:
	rm -f $(LIB_APRIL_JSERIAL_OBJS) *~ *.so $(LIB) $(DESTLIB)

%.o: %.c
	$(CC) $(CCFLAGS) $(JNI_INCLUDES) $<
