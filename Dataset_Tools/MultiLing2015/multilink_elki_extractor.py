"""
Using the MultiLing 2015 dataset files, extract the ground truth clusters in 
the format ELKI expects them, and also the distance matrix for ELKI, for
clustering using the main program's similarity. Python 3
"""

import csv
import os


def get_text_categories(path):
    text_types = {}

    files = os.listdir(path)

    for file in files:
        # From filename Mxxxy.LANG keep only xxx (the category)
        category = file.split(".")[0].replace("M", "")[:3]
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

    # Get categories of texts of MultiLing dataset
    text_categories = get_text_categories(texts_folder)

    with open(input_csv) as f:
        reader = csv.reader(f, delimiter=",", quoting=csv.QUOTE_NONE)

        # Read headers & find index of column that will be used for clustering
        headers = next(reader)

        elki_index = headers.index(clustering_column)

        # Read data rows
        for row in reader:
            pass
            # todo


if __name__ == "__main__":
    main()
