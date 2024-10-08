COMPATIBLE_MACHINE:class-target = "(cuda)"
COMPATIBLE_HOST = "(x86_64|aarch64)"
CUDA_VERSION ??= "10.2"
CUDA_FULL_VERSION ?= "${PV}"

S = "${WORKDIR}/${BP}"
B = "${S}"

PR = "r0"

CUDA_DEB_PKGARCH = "${@cuda_deb_pkg_arch(d)}"

def cuda_deb_pkg_arch(d):
    arch = d.getVar('HOST_ARCH')
    if arch == 'x86_64':
        return 'amd64'
    elif arch == 'aarch64':
        return 'arm64'
    bb.error("unrecognized HOST_ARCH: %s" % arch)
    return 'UNKNOWN'

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_SYSROOT_STRIP = "1"
