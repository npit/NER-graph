import os
import csv
import string
import MUC3Utils


def get_text_type(path, file):
    # Open the file
    with open(path + file, "r") as f:
        content = f.read()

        # Create HTML parser to get the dc.content type
        parser = MUC3Utils.CategoryExtractor()
        parser.feed(content)

        return parser.data


def get_text_categories(path):
    # Dictionary to keep types for each text
    text_types = {}

    files = os.listdir(path)

    for file in files:
        text_type = get_text_type(path, file)
        text_types[file] = text_type

    return text_types


def main():
    # GENERAL SETTINGS
    clustering_column = "PH (A) NVS"

    # INPUT SETTINGS
    texts_folder = "../texts/input/"
    input_csv = "../out-100.csv"

    # OUTPUT SETTINGS
    output_csv = "gold.csv"
    elki_distance_matrix_path = "elki_distance_matrix.txt"
    elki_text_ids_path = "elki_text_ids.txt"
    ground_truth_clusters_path = "ground_truth_clusters.txt"

    # Variables used for ELKI output and stats
    same = 0
    different = 0
    coverages = {}
    coverage_ids = {}
    counted_texts = []
    word_diffs = {
        "same": [],
        "diff": []
    }
    ratios = {
        "same": [],
        "diff": []
    }

    text_ids = {}
    elki_array = []

    # Get the "category" of each text from the DC.coverage attribute of MUC3
    text_categories = get_text_categories(texts_folder)

    print("Successfully gotten categories, starting GOLD calculation...")

    # Check that the CSV to read exists
    if os.path.isfile(input_csv):
        with open(input_csv) as f:
            with open(output_csv, "w", newline="") as out:
                # Read the CSV
                reader = csv.reader(f, delimiter=",", quoting=csv.QUOTE_NONE)
                writer = csv.writer(out, delimiter=",", quoting=csv.QUOTE_NONE)

                # Add headers to the new csv file
                headers = next(reader)
                headers.append("GOLD")
                headers.append("text1 wcount")
                headers.append("text2 wcount")
                headers.append("word difference")
                writer.writerow(headers)

                elki_index = headers.index(clustering_column)

                # Read data rows
                for row in reader:
                    # Get text titles
                    text1 = row[0]
                    text2 = row[1]

                    # Get values for elki
                    if text1 not in text_ids:
                        text_ids[text1] = len(text_ids)
                    if text2 not in text_ids:
                        text_ids[text2] = len(text_ids)

                    elki_array.append(
                        str(text_ids[text1]) + " " +
                        str(text_ids[text2]) + " " +
                        str((1.0 - float(row[elki_index]))) + "\n")

                    # If text categories are the same, write a 1, otherwise 0
                    new_row = row

                    # Get each text as a string
                    with open(texts_folder + text1, 'r') as f:
                        text1str = f.read().replace('\n', '')

                    with open(texts_folder + text2, 'r') as f:
                        text2str = f.read().replace('\n', '')

                    # Keep only text from HTML (MUC3 is html format)
                    parser = MUC3Utils.TextExtractor()
                    parser.feed(text1str)
                    text1data = parser.data

                    parser = MUC3Utils.TextExtractor()
                    parser.feed(text2str)
                    text2data = parser.data

                    # Remove text titles from the texts
                    text1_name_no_extension = row[0].split(".")[0]
                    text2_name_no_extension = row[1].split(".")[0]
                    text1data = text1data.replace(text1_name_no_extension, "")
                    text2data = text2data.replace(text2_name_no_extension, "")

                    # Remove punctuation
                    for c in string.punctuation:
                        text1data = text1data.replace(c, " ")
                        text2data = text2data.replace(c, " ")

                    # Split texts into arrays
                    text1array = text1data.split(" ")
                    text2array = text2data.split(" ")

                    # Remove empty strings
                    text1array = [x.strip() for x in text1array if x.strip()]
                    text2array = [x.strip() for x in text2array if x.strip()]

                    # Find ratio (idea: http://stackoverflow.com/a/29929179)
                    common_items = set(text1array) & set(text2array)
                    all_items = set(text1array) | set(text2array)

                    common_to_all = len(common_items) / float(len(all_items))

                    # Calculate the word difference
                    diff = len(text1array) - len(text2array)
                    if diff < 0:
                        diff *= -1

                    # Add similarity depending mainly on the DC.coverage from
                    # the MUC3 dataset, but also the word overlap
                    if text_categories[text1] == text_categories[text2]:
                        # Categories are the same, minimum 0.5 similarity
                        similarity = 0.5 + (common_to_all / 2.0)

                        # Keep stats for same category texts
                        same += 1
                        word_diffs["same"].append(diff)
                        ratios["same"].append(common_to_all)
                    else:
                        # Categories different, maximum 0.5 similarity
                        similarity = common_to_all / 2.0

                        # Keep stats for different cateogry texts
                        different += 1
                        word_diffs["diff"].append(diff)
                        ratios["diff"].append(common_to_all)

                        # Get text types of texts to count them
                        for i in range(0, 2):
                            # Get text filename
                            text_name = row[i]

                            # Count texts only once
                            if text_name in counted_texts:
                                continue
                            else:
                                counted_texts.append(text_name)

                            # Get text type
                            # text_type = get_text_type(texts_folder, row[i])
                            text_type = text_categories[text_name]

                            # Check which length
                            text_id = text_ids[text_name]
                            if i == 0:
                                length = len(text1array)
                            else:
                                length = len(text2array)

                            # Add to existing array or create new
                            if text_type in coverages:
                                coverages[text_type].append(length)
                                coverage_ids[text_type].append(text_id)
                            else:
                                coverages[text_type] = []
                                coverages[text_type].append(length)
                                coverage_ids[text_type] = []
                                coverage_ids[text_type].append(text_id)

                    # Add calculated values to the exported CSV
                    new_row.append(similarity)  # Similarity
                    new_row.append(len(text1array))  # Text 1 word count
                    new_row.append(len(text2array))  # Text 2 word count
                    new_row.append(diff)  # Word difference

                    # Write this row to the new csv
                    writer.writerow(new_row)

    # Print stats
    print("Number of different DC.coverage values: " + str(len(coverages)))

    print("\nComparisons of texts with SAME category:")
    print("=> Number of comparisons: " + str(same))
    print("=> Average word count difference: " +
          str(sum(word_diffs["same"]) / len(word_diffs["same"])))
    print("=> Average same/total word ratio: " +
          str(sum(ratios["same"]) / len(ratios["same"])))

    print("\nComparisons of texts with DIFFERENT category:")
    print("=> Number of comparisons: " + str(different))
    print("=> Average word count difference: " +
          str(sum(word_diffs["diff"]) / len(word_diffs["diff"])))
    print("=> Average same/total word ratio: " +
          str(sum(ratios["diff"]) / len(ratios["diff"])))
    print()

    # Add similarity of each text to itself in ELKI matrix (required)
    # https://elki-project.github.io/howto/precomputed_distances#using-an-external-distance
    texts_num = len(text_ids)
    for x in range(0, texts_num):
        # distance 0 with itself
        elki_array.append(str(x) + " " + str(x) + " 0.0\n")

    # Write file for elki distance matrix
    with open(elki_distance_matrix_path, "w") as elki_f:
        elki_f.writelines(elki_array)

    with open(elki_text_ids_path, "w") as elki_f:
        for text in text_ids:
            elki_f.write(text + " " + str(text_ids[text]) + "\n")

    # Write file for use in importing "ground truth" clusters to Java program
    with open(ground_truth_clusters_path, "w") as gt_f:
        for coverage in coverage_ids:
            gt_f.write(coverage + "|" + str(" ".join(
                str(item) for item in coverage_ids[coverage])) + "\n")

    print("Done!")


if __name__ == '__main__':
    main()
