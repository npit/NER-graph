"""
After using the renamer script, use this to extract the ground truth clusters from the renamed files
and the distance matrix for ELKI (by using the CSV which contains the comparisons of the files)

Dataset from: http://qwone.com/~jason/20Newsgroups/
"""

import csv

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
    """
    Note: this function is the same as in multilink_elki_extractor.
    Maybe we can merge them?
    """
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
                text_id = text_ids[text_name]

                # Add the text to the ELKI distance matrix line
                elki_array_line += str(text_id) + " "

                # Get text's category
                text_type = text_categories[text_name]

                # Create category if it doesn't exist, and add text to it
                if text_type not in category_contents:
                    category_contents[text_type] = []

                if text_id not in category_contents[text_type]:
                    category_contents[text_type].append(text_id)

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

    # Write file with filename -> ELKI ID mapping
    with open(elki_text_ids_path, "w") as elki_f:
        for text in text_ids:
            elki_f.write(text + " " + str(text_ids[text]) + "\n")

    # Write file for use in importing ground truth clusters to Java program
    with open(ground_truth_clusters_path, "w") as gt_f:
        for cluster_name in category_contents:
            gt_f.write(cluster_name + "|" + str(" ".join(
                str(item) for item in category_contents[cluster_name])) + "\n")


if __name__ == "__main__":
    main()
