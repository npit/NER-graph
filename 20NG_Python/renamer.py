"""
Script to rename the texts of the 20 Newsgroups dataset to have unique names by
appending the news item's group to its filename.

Dataset from: http://qwone.com/~jason/20Newsgroups/
"""
import os


def main():
    # Directories of test & train folders
    dirs = {
        "test": "20NG_data/20news-bydate-test",
        "train": "20NG_data/20news-bydate-train"
    }

    # For each directory (train, test) rename their texts
    for dir_key in dirs:
        print("Processing: " + dir_key)
        dir_path = dirs[dir_key]

        if os.path.isdir(dir_path):
            for group in os.listdir(dir_path):
                # Get full path of directory
                group_path = os.path.abspath(dir_path + "/" + group)

                # Rename all files
                [os.rename(group_path + "\\" + f,
                           group_path + "\\" + dir_key + "_" + group + "_" + f)
                 for f in os.listdir(group_path)]


if __name__ == "__main__":
    main()
