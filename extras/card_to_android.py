#!/usr/bin/env python

#  Takes an image (presumably a Dungeon Degenerates skill card scanned at
#  600 DPI, with a 900 x 1400 selection) and does the following:
#
#  - Attempt to smooth out the halftone by Filters -> Blur -> Selective
#    Gaussian Blur, Blur Radius 10.0, Max. Delta 80
#  - Resize to 360 x 560, save in drawable-xhdpi
#  - Resize to 270 x 420, save in drawable-hdpi
#  - Resize to 180 x 280, save in drawble-mdpi
#  - Resize to 135 x 210, save in drawable-ldpi
#
#  I am not much of a Python-Fu master; I may hate Python more than Scheme,
#  which I sure hate a lot.

import os
from gimpfu import *

def saveScaled(img, drawable, newWidth, newHeight, subdirName,
                fileName, outputDirectory, resPrefix):

    scaled = drawable #drawable.copy(False)
    #  after drawable.copy(False), scaled.scale() croaks
    scaled.scale(newWidth, newHeight)

    path = outputDirectory + "/" + resPrefix + subdirName
    if os.path.exists(path) != True:
        #nuts, python3
        #raise FileNotFoundError(path)
        raise OSError(path + " doesn't exist!")

    fullpath = path + "/" + fileName + ".png"
    if os.path.exists(fullpath):
        #nuts, python3
        #raise FileExistsError(fullpath)
        raise OSError(fullpath + " already exists!")

    pdb.file_png_save(img, scaled, fullpath, fullpath, 0, 9, 0, 0, 0, 0, 0)


#  cardType is 0 - skill, 1 - mastery, 2 - danger
def card_to_android(img, drawable, name, directory,
                    resPrefix="drawable-", cardType=0,
                    blur=True, blurRadius=10.0, blurMaxDelta=80,
                    xhdpi=True, hdpi=True, mdpi=True, ldpi=True):
    if cardType == 1:
        filename = "mastery_" + name;
    elif cardType == 2:
        filename = "danger_" + name;
    else:
        filename = "skill_" + name;

    #  Copy the current selection into a new image.
    pdb.gimp_edit_copy(drawable)
    newImage = pdb.gimp_edit_paste_as_new()
    newDrawable = newImage.active_layer

    if blur:
        pdb.plug_in_sel_gauss(newImage, newDrawable, blurRadius, blurMaxDelta)

    if xhdpi:
        saveScaled(newImage, newDrawable, 360, 560, "xhdpi",
                filename, directory, resPrefix)
    if hdpi:
        saveScaled(newImage, newDrawable, 270, 420, "hdpi",
                filename, directory, resPrefix)
    if xhdpi:
        saveScaled(newImage, newDrawable, 180, 280, "mdpi",
                filename, directory, resPrefix)
    if xhdpi:
        saveScaled(newImage, newDrawable, 135, 210, "ldpi",
                filename, directory, resPrefix)

register(
        "card_to_android",
        "Export a scanned DUNGEON DEGENERATES skill card to a series of scaled images for Android.",
        "Export a scanned DUNGEON DEGENERATES skill card to a series of scaled images for Android.",
        "kuhrusty",
        "kuhrusty",
        "",
        "<Image>/Android/Card To Android",
        "RGB*",
        [
            (PF_STRING, "name", "Card ID", ""),
            (PF_DIRNAME,"directory", "Directory", "src/main/res"),
            (PF_STRING, "resPrefix", "res subdir prefix", "drawable-"),
            (PF_OPTION, "cardType", "Card type", 0,
                        ["Skill", "Mastery", "Danger"]),
            (PF_BOOL,   "blur", "Selective blur", True),
            (PF_FLOAT,  "blurRadius", "Blur radius", 10.0),
            (PF_INT,    "blurMaxDelta", "Blur max delta", 80),
            (PF_BOOL,   "xhdpi", "Generate xhdpi", True),
            (PF_BOOL,   "hdpi",  "Generate hdpi", True),
            (PF_BOOL,   "mhdpi", "Generate mdpi", True),
            (PF_BOOL,   "lhdpi", "Generate ldpi", True)
        ],
        [],
        card_to_android)

main()
