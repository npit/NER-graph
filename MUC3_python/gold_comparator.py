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
    path = "../texts/input-25/"
    csv_path = "../out-25.csv"

    temp_file = "temp.csv"

    # Get the "category" of each text from the DC.coverage attribute of MUC3
    text_categories = get_text_categories(path)

    print("Successfully gotten categories, starting GOLD calculation...")

    # Check that the CSV to read exists
    if os.path.isfile(csv_path):
        with open(csv_path) as f:
            with open(temp_file, "w", newline="") as out:
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

                # Read data rows
                for row in reader:
                    # Get text titles
                    text1 = row[0]
                    text2 = row[1]

                    # If text categories are the same, write a 1, otherwise 0
                    new_row = row

                    # Get each text as a string
                    with open(path + text1, 'r') as f:
                        text1str = f.read().replace('\n', '')

                    with open(path + text2, 'r') as f:
                        text2str = f.read().replace('\n', '')

                    # Keep only text from HTML (MUC3 is html format)
                    parser = MUC3Utils.TextExtractor()
                    parser.feed(text1str)
                    text1data = parser.data

                    parser = MUC3Utils.TextExtractor()
                    parser.feed(text2str)
                    text2data = parser.data

                    # Remove text titles from the texts
                    text1_filename_without_ext = row[0].split(".")[0]
                    text2_filename_without_ext = row[1].split(".")[0]
                    text1data = text1data.replace(text1_filename_without_ext,
                                                  "")
                    text2data = text2data.replace(text2_filename_without_ext,
                                                  "")
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

                    # Add similarity depending mainly on the DC.coverage from
                    # the MUC3 dataset, but also the word overlap
                    if text_categories[text1] == text_categories[text2]:
                        # Categories are the same, minimum 0.5 similarity
                        new_row.append(0.5 + (common_to_all / 2.0))
                    else:
                        # Categories different, maximum 0.5 similarity
                        new_row.append(0 + (common_to_all / 2.0))

                    # Add text word counts
                    new_row.append(len(text1array))
                    new_row.append(len(text2array))

                    # Add word difference
                    diff = len(text1array) - len(text2array)
                    if diff < 0:
                        diff *= -1
                    new_row.append(diff)

                    # Write this row to the new csv
                    writer.writerow(new_row)
    print("Done!")


if __name__ == '__main__':
    main()
