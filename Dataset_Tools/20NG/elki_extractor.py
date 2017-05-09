"""
After using the renamer script, use this to extract the ground truth clusters from the renamed files
and the distance matrix for ELKI (by using the CSV which contains the comparisons of the files)

Dataset from: http://qwone.com/~jason/20Newsgroups/
"""

import os


def get_text_categories(path):
    text_types = {}

    files = os.listdir(path)

    for file in files:
        # From filename eg. "test_misc.forsale_76427" keep "misc.forsale"
        category = file.split("_")[1]
        text_types[file] = category

    return text_types


def main():
    # GENERAL SETTINGS
    clustering_column = "PH (A) NVS"

    # INPUT SETTINGS
    texts_folder = "../../texts/input/"
    input_csv = "../../out.csv"

    # OUTPUT SETTINGS
    elki_distance_matrix_path = "elki_distance_matrix.txt"
    elki_text_ids_path = "elki_text_ids.txt"
    ground_truth_clusters_path = "ground_truth_clusters.txt"

    # ELKI variables
    text_ids = {}
    elki_array = []
    category_contents = {}

    # Get categories of texts of 20NG dataset
    text_categories = get_text_categories(texts_folder)


if __name__ == "__main__":
    main()
