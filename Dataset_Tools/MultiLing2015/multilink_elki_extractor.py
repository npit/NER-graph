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

    # ELKI variables
    text_ids = {}
    elki_array = []

    # Get categories of texts of MultiLing dataset
    text_categories = get_text_categories(texts_folder)

    with open(input_csv) as f:
        reader = csv.reader(f, delimiter=",", quoting=csv.QUOTE_NONE)

        # Read headers & find index of column that will be used for clustering
        headers = next(reader)

        elki_index = headers.index(clustering_column)

        # Read data rows
        for row in reader:
            elki_array_line = ""

            for text_num in range(0, 2):
                # Get the text's filename from the CSV
                text_name = row[text_num]

                # Create text ID if it doesn't have one already
                if text_name not in text_ids:
                    text_ids[text_name] = len(text_ids)

                # Add the text to the ELKI distance matrix line
                elki_array_line += str(text_ids[text_name]) + " "

            # Finish the elki array line and append it to the array
            elki_array_line += str(1.0 - float(row[elki_index])) + "\n"
            elki_array.append(elki_array_line)

    # Add similarity of each text to itself in ELKI matrix (required)
    # https://elki-project.github.io/howto/precomputed_distances#using-an-external-distance
    texts_num = len(text_ids)
    for x in range(0, texts_num):
        # distance 0 with itself
        elki_array.append(str(x) + " " + str(x) + " 0.0\n")

    # Write file for elki distance matrix
    with open(elki_distance_matrix_path, "w") as elki_f:
        elki_f.writelines(elki_array)


if __name__ == "__main__":
    main()
