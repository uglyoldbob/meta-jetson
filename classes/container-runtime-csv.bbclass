CONTAINER_CSV_FILES ??= "${libdir}/*.so*"
CONTAINER_CSV_EXCLUDE_FILES ??= ""
CONTAINER_CSV_BASENAME ??= "${PN}"
CONTAINER_CSV_PKGNAME ?= "${CONTAINER_CSV_BASENAME}-container-csv"
CONTAINER_CSV_EXTRA ??= ""

python populate_container_csv() {
    import os
    import glob
    globs = (d.getVar('CONTAINER_CSV_FILES') or '').split()
    if len(globs) == 0:
        return
    root = d.getVar('D')
    entries = set()
    oldcwd = os.getcwd()
    os.chdir(root)
    for csvglob in globs:
        if os.path.isabs(csvglob):
            csvglob = '.' + csvglob
        if not csvglob.startswith('./'):
            csvglob = './' + csvglob
        globbed = glob.glob(csvglob)
        if globbed and globbed != [ csvglob ]:
            entries.update([entry[2:] for entry in globbed])
        else:
            entries.update([csvglob[2:]])
    excludeglobs = (d.getVar('CONTAINER_CSV_EXCLUDE_FILES') or '').split()
    for csvglob in excludeglobs:
        if os.path.isabs(csvglob):
            csvglob = '.' + csvglob
        if not csvglob.startswith('./'):
            csvglob = './' + csvglob
        globbed = glob.glob(csvglob)
        if globbed and globbed != [ csvglob ]:
            for entry in globbed:
                entries.discard(entry[2:])
        else:
            for entry in globbed:
                entries.discard(csvglob[2:])
    csvlines = (d.getVar('CONTAINER_CSV_EXTRA') or '').split('\n')
    for entry in entries:
        if os.path.isdir(entry):
            csvtype = "dir"
        elif os.path.islink(entry):
            csvtype = "sym"
        elif os.path.isfile(entry):
            csvtype = "lib"
        else:
            bb.warn("Unrecognized file type for container CSV: {}".format(entry))
            continue
        entry = "/" + entry
        # CONTAINER_CSV_LIB_PATH can be set to /usr/lib/aarch64-linux-gnu/ to avoid glibc version mismatch
        # See the l4t.csv file at https://github.com/OE4T/meta-tegra/pull/1194
        # It's done differently because in this version of the code the csv file is generated and not statically defined
        custom_lib_path = d.getVar('CONTAINER_CSV_LIB_PATH')
        if custom_lib_path:
            if entry.startswith('/usr/lib/') and not entry.startswith(custom_lib_path):
                entry = entry.replace('/usr/lib/', custom_lib_path)
        csvlines.append("{}, {}".format(csvtype, entry))

    os.chdir(oldcwd)
    csvfiledir = os.path.join(root, d.getVar('sysconfdir')[1:], 'nvidia-container-runtime',
                              'host-files-for-container.d')
    bb.utils.remove(csvfiledir, recurse=True)
    bb.utils.mkdirhier(csvfiledir)
    csvfile = os.path.join(csvfiledir, d.getVar('PN') + '.csv')
    with open(csvfile, 'w', encoding='utf-8') as outf:
        outf.write('\n'.join(sorted(csvlines)) + '\n')
    os.chmod(csvfile, 0o644)
}

CONTAINERCSVFUNC = ""
CONTAINERCSVFUNC:tegra = "populate_container_csv"
do_install[postfuncs] += "${CONTAINERCSVFUNC}"

PACKAGES:prepend:tegra = " ${CONTAINER_CSV_PKGNAME} "
FILES:${CONTAINER_CSV_PKGNAME} = "${sysconfdir}/nvidia-container-runtime"
RDEPENDS:${PN}:append:tegra = " ${@bb.utils.contains('DISTRO_FEATURES', 'virtualization', '${CONTAINER_CSV_PKGNAME}', '', d)}"
