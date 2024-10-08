PACKAGESPLITFUNCS:prepend = " split_gstreamer10_packages "
PACKAGESPLITFUNCS:append = " set_metapkg_rdepends "

python split_gstreamer10_packages () {
    gst_libdir = d.expand('${libdir}/gstreamer-${LIBV}')
    postinst = d.getVar('plugin_postinst')
    glibdir = d.getVar('libdir')

    do_split_packages(d, glibdir, r'^lib(.*)\.so\.*', 'lib%s', 'gstreamer %s library', extra_depends='', allow_links=True)
    do_split_packages(d, gst_libdir, r'libgst(.*)\.so$', d.expand('${PN}-%s'), 'GStreamer plugin for %s', postinst=postinst, extra_depends='')
    do_split_packages(d, glibdir+'/girepository-1.0', r'Gst(.*)-1.0\.typelib$', d.expand('${PN}-%s-typelib'), 'GStreamer typelib file for %s', postinst=postinst, extra_depends='')
    do_split_packages(d, gst_libdir, r'libgst(.*)\.la$', d.expand('${PN}-%s-dev'), 'GStreamer plugin for %s (development files)', extra_depends='${PN}-dev')
    do_split_packages(d, gst_libdir, r'libgst(.*)\.a$', d.expand('${PN}-%s-staticdev'), 'GStreamer plugin for %s (static development files)', extra_depends='${PN}-staticdev')
}

python set_metapkg_rdepends () {
    import os
    import oe.utils

    pn = d.getVar('PN')
    metapkg =  pn + '-meta'
    d.setVar('ALLOW_EMPTY:' + metapkg, "1")
    d.setVar('FILES:' + metapkg, "")
    blacklist = [ pn, pn + '-meta' ]
    metapkg_rdepends = []
    pkgdest = d.getVar('PKGDEST')
    for pkg in oe.utils.packages_filter_out_system(d):
        if pkg not in blacklist and pkg not in metapkg_rdepends:
            # See if the package is empty by looking at the contents of its PKGDEST subdirectory. 
            # If this subdirectory is empty, then the package is.
            # Empty packages do not get added to the meta package's RDEPENDS
            pkgdir = os.path.join(pkgdest, pkg)
            if os.path.exists(pkgdir):
                dir_contents = os.listdir(pkgdir) or []
            else:
                dir_contents = []
            is_empty = len(dir_contents) == 0
            if not is_empty:
                metapkg_rdepends.append(pkg)
    d.setVar('RDEPENDS:' + metapkg, ' '.join(metapkg_rdepends))
    d.setVar('DESCRIPTION:' + metapkg, pn + ' meta package')
}

# each plugin-dev depends on PN-dev, plugin-staticdev on PN-staticdev
# so we need them even when empty (like in gst-plugins-good case)
ALLOW_EMPTY:${PN} = "1"
ALLOW_EMPTY:${PN}-dev = "1"
ALLOW_EMPTY:${PN}-staticdev = "1"

PACKAGES += "${PN}-apps ${PN}-meta ${PN}-glib"

# Dynamically generate packages for all enabled plugins
PACKAGES_DYNAMIC = "^${PN}-.* ^libgst.*"

FILES:${PN} = ""
FILES:${PN}-apps = "${bindir}"
FILES:${PN}-glib = "${datadir}/glib-2.0"

RRECOMMENDS:${PN} += "${PN}-meta"
