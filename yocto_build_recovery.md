# Yocto Build Recovery Guide: glib-2.0 & PCRE Linker Fixes
**Project Reference:** Assignment 9 Yocto (`cortexa57-poky-linux`)

## 1. The Definitive local.conf Block
If your configurations get wiped or desynced, paste this exact block at the very bottom of your `build/conf/local.conf` file:

```bitbake
# Force BitBake to strip host environment variables to prevent basehash fluctuations
BB_PRESERVE_ENV = "0"

# Target Linker Paths: Map both application (/usr/lib) and base system (/lib) directories
TARGET_LDFLAGS:append:pn-glib-2.0 = " -Wl,-rpath-link=\({STAGING_LIBDIR} -Wl,-rpath-link=\){STAGING_BASELIBDIR}"

# Host/Native Linker Paths: Map the isolated host-tool tracking libraries
BUILD_LDFLAGS:append:pn-glib-2.0-native = " -Wl,-rpath-link=\({STAGING_LIBDIR_NATIVE} -Wl,-rpath-link=\){STAGING_BASELIBDIR_NATIVE}"

# Mask out internal unit tests to prevent crashes on the gtester utility tool
EXTRA_OEMESON:append:pn-glib-2.0 = " -Dtests=false"
EXTRA_OEMESON:append:pn-glib-2.0-native = " -Dtests=false"

# Automatically delete temporary object files to save up to 80% disk space
INHERIT += "rm_work"
```

## 2. Emergency Recovery Steps (If the PC crashes or basehash error returns)
If your terminal locks up, your PC restarts, or the "basehash value changed" error pops up, execute this exact sequence in order:

1. **Close the broken terminal completely** and open a brand-new terminal window.
2. Go to your repository path and source the environment:
   ```bash
   cd /home/artb/git/assignment-9-yocto/
   source poky/oe-init-build-env build
   ```
3. Strip away host library paths to stop environment leaks:
   ```bash
   unset LD_LIBRARY_PATH
   unset PKG_CONFIG_PATH
   unset PKG_CONFIG_SYSROOT_DIR
   ```
4. Wipe out the corrupted metadata lockfiles and text-parsing cache databases:
   ```bash
   killall -9 bitbake bitbake-worker 2>/dev/null || true
   rm -rf cache/ bitbake.lock
   ```
5. Run the localized source compilation loop to generate healthy binary states:
   ```bash
   bitbake --no-setscene glib-2.0-native glib-2.0
   ```

## 3. Key Troubleshooting Rules Developed
* **Never use `rm -rf tmp/work/...` manually:** It breaks BitBake's task-tracking architecture. Always use `bitbake -c clean <recipe>`.
* **Never add `ninja` to `HOSTTOOLS`:** Doing so leaks your host PC's compilation settings into Yocto. Let the `meson` class use its internal `ninja-native` tool automatically.
* **The difference between `/lib` and `/usr/lib`:** Core system runtime libraries (`libz.so.1`, `libmount.so.1`) live in the base system `/lib` folder (`${STAGING_BASELIBDIR}`), while frameworks (`libpcre.so.1`) live in `/usr/lib` (`${STAGING_LIBDIR}`). Both paths must be explicitly passed to the linker.

