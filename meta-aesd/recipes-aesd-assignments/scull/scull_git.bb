SUMMARY = "Scull kernel modules"
# See https://git.yoctoproject.org/poky/tree/meta/files/common-licenses
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

inherit update-rc.d module

# TODO: Set this  with the path to your assignments rep.  Use ssh protocol and see lecture notes
# about how to setup ssh-agent for passwordless access
SRC_URI = "git://github.com/cu-ecen-aeld/assignment-7-artb83.git;protocol=ssh;branch=master"

PV = "1.0+git${SRCPV}"
# TODO: set to reference a specific commit hash in your assignment repo
SRCREV = "a8ce18f45a9112280625f38023533eabf94611d2"

# This sets your staging directory based on WORKDIR, where WORKDIR is defined at 
# https://docs.yoctoproject.org/ref-manual/variables.html?highlight=workdir#term-WORKDIR
# We reference the "aesd-char-driver" directory here to build from the "aesd-char-driver" directory
# in your assignments repo

S = "${WORKDIR}/git/scull"

INITSCRIPT_PACKAGES = "${PN}"
INITSCRIPT_NAME:${PN} = "scull-module-load-unload"
INITSCRIPT_PARAMS:${PN} = "defaults 60"
FILES:${PN} += "${sysconfdir}/init.d/*"
FILES:${PN} += "${bindir}"

EXTRA_OEMAKE = "KERNEL_SRC=${STAGING_KERNEL_BUILDDIR} M=${S} EXTRA_CFLAGS='-std=gnu99 -Wno-declaration-after-statement -I${S}/../include'"

do_compile() {
    oe_runmake -C ${STAGING_KERNEL_BUILDDIR} M=${S} modules
}

do_install(){
	install -d ${D}/lib/modules/${KERNEL_VERSION}/extra
	install -m 0644 ${S}/scull.ko  ${D}/lib/modules/${KERNEL_VERSION}/extra
	
	install	-d ${D}${bindir}
    install -m 0755 ${S}/scull_load	    ${D}${bindir}/scull_module_load
	install -m 0755 ${S}/scull_unload	${D}${bindir}/scull_module_unload
	
	install -d ${D}${sysconfdir}/init.d
	install -m 0755 ${S}/scull-module-load-unload  ${D}${sysconfdir}/init.d/scull-module-load-unload
}


