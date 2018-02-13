meta-axxia-rdk
==============

This layer adds build instructions to the meta-intel-axxia layer to create an
'in-distribution' image.

When applied, meta-axxia-rdk provides support for creating a distribution that
adds out-of-tree RDK kernel level modules to the resulting image. The RDK kernel
modules also get loaded on boot-up.

Use of this layer assumes that you are being supplied with the relevant kernel
module source code release archives directly from Intel.

Updates to meta-intel-axxia README
============================

This section contains updates and additions to the meta-intel-axxia README, needed 
to apply this layer.

## Sources

The Intel github.com repositories provide meta-axxia-rdk. To access the
private repository, request permission from Intel. Note that the
private repository is used for development and is not supported.

git clone https://github.com/axxia/meta-axxia-rdk_private.git meta-axxia-rdk

The public Intel repository contains _TODO: confirm purpose_

git clone https://github.com/axxia/meta-axxia-rdk.git

In all cases, use the 'rocko' branch. The commit used as HEAD for a
particular release will be listed in the release notes.


## Building the meta-intel-axxia BSP layer

3a: Clone the Axxia RDK meta layer. This provides meta data for building
in-distribution images for the Axxia specific board types.  See 'Sources' above to
select the right meta-intel-axxia repository, branch, and version.

```
   $ cd $YOCTO/poky
   $ <the git clone command chosen above>
   $ cd meta-axxia-rdk
   $ git checkout rocko
   $ mkdir downloads
   $ cd downloads
   $ cp /your/path/to/rdk_klm_src_<releaseinfo>.txz .
   $ ln -s rdk_klm_src_<releaseinfo>.txz rdk_klm_src.tar.xz
```

8:  Edit the conf/bblayers.conf file

```
   $ pwd (you should be at $YOCTO/axxia)
   $ vi conf/bblayers.conf
```

Edit BBLAYERS variable as follows. Replace references to $YOCTO below with the
actual value you provided in step 1.

```
   BBLAYERS ?= " \
            $YOCTO/poky/meta \
            $YOCTO/poky/meta-poky \
            $YOCTO/poky/meta-openembedded/meta-oe \
            $YOCTO/poky/meta-openembedded/meta-python \
            $YOCTO/poky/meta-openembedded/meta-networking \
            $YOCTO/poky/meta-virtualization \
            $YOCTO/poky/meta-intel \
            $YOCTO/poky/meta-intel-axxia \
            $YOCTO/poky/meta-intel-axxia/meta-intel-snr \
            $YOCTO/poky/meta-axxia-rdk \
            "
```
9: Edit the conf/local.conf file:

```
   $ vi conf/local.conf
```

9.1: Set distribution configuration to have all Axxia specific features.

    DISTRO = "intel-axxia-indist"
