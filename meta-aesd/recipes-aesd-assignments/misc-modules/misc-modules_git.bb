SUMMARY = "Misc kernel modules"
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


S = "${WORKDIR}/git/misc-modules"

INITSCRIPT_PACKAGES = "${PN}"
INITSCRIPT_NAME:${PN} = "misc-faulty-hello-modules-load-unload"
INITSCRIPT_PARAMS:${PN} = "defaults 60"
FILES:${PN} += "${sysconfdir}/init.d/*"
FILES:${PN} += "${bindir}"

EXTRA_OEMAKE = "KERNEL_SRC=${STAGING_KERNEL_BUILDDIR} M=${S} EXTRA_CFLAGS='-std=gnu99 -Wno-declaration-after-statement -I${S}/../include'"

do_compile() {
    oe_runmake -C ${STAGING_KERNEL_BUILDDIR} modules
}

do_install(){
	install -d ${D}/lib/modules/${KERNEL_VERSION}/extra
	install -m 0644 ${S}/hello.ko   ${D}/lib/modules/${KERNEL_VERSION}/extra
	install -m 0644 ${S}/faulty.ko  ${D}/lib/modules/${KERNEL_VERSION}/extra

    install	-d ${D}${bindir}
    install -m 0755 ${S}/module_load	${D}${bindir}/misc_module_load
	install -m 0755 ${S}/module_unload	${D}${bindir}/misc_module_unload
	
	install -d ${D}${sysconfdir}/init.d
	install -m 0755 ${S}/misc-faulty-hello-modules-load-unload  ${D}${sysconfdir}/init.d/misc-faulty-hello-modules-load-unload
}


