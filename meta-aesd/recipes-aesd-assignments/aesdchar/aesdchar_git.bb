# See https://git.yoctoproject.org/poky/tree/meta/files/common-licenses
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

inherit update-rc.d module

# TODO: Set this  with the path to your assignments rep.  Use ssh protocol and see lecture notes
# about how to setup ssh-agent for passwordless access
SRC_URI = "git://github.com/cu-ecen-aeld/assignments-3-and-later-artb83.git;protocol=ssh;branch=master"

PV = "1.0+git${SRCPV}"
# TODO: set to reference a specific commit hash in your assignment repo
SRCREV = "9297d1436ceae39868c2a99ead4c900356e8e7cf"

# This sets your staging directory based on WORKDIR, where WORKDIR is defined at 
# https://docs.yoctoproject.org/ref-manual/variables.html?highlight=workdir#term-WORKDIR
# We reference the "aesd-char-driver" directory here to build from the "aesd-char-driver" directory
# in your assignments repo

S = "${WORKDIR}/git/aesd-char-driver"

INITSCRIPT_PACKAGES = "${PN}"
INITSCRIPT_NAME:${PN} = "aesdchar_load_unload"
INITSCRIPT_PARAMS:${PN} = "defaults 60"
FILES:${PN} += "${sysconfdir}/init.d/*"
FILES:${PN} += "${bindir}"


EXTRA_OEMAKE = "KERNEL_SRC=${STAGING_KERNEL_BUILDDIR} M=${S} EXTRA_CFLAGS='-std=gnu99 -Wno-declaration-after-statement'"

do_compile() {
    oe_runmake -C ${STAGING_KERNEL_BUILDDIR} M=${S} modules
}

do_install(){
	install -d ${D}/lib/modules/${KERNEL_VERSION}/extra
	install -m 0644 ${S}/aesdchar.ko  ${D}/lib/modules/${KERNEL_VERSION}/extra
	
	install	-d ${D}${sysconfdir}/init.d
	install -m 0755 ${S}/aesdchar_load_unload  ${D}${sysconfdir}/init.d/aesdchar_load_unload
	
	install	-d ${D}${bindir}
	install -m 0755 ${S}/aesdchar_unload  ${D}${bindir}/aesdchar_unload
	install -m 0755 ${S}/drivertest.sh	  ${D}${bindir}/aesdchar_drivertest.sh
	install -m 0755 ${S}/aesdchar_load    ${D}${bindir}/aesdchar_load
}



