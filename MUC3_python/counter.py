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


def main():
    temp_file = "temp-25.csv"
    texts_path = "../texts/input-25/"
    print_individual_texts = False

    same = 0
    different = 0
    coverages = {}
    counted_texts = []
    word_diffs = {
        "same": [],
        "diff": []
    }
    ratios = {
        "same": [],
        "diff": []
    }

    with open(temp_file, "r", newline="") as f:
        # Read the CSV
        reader = csv.reader(f, delimiter=",", quoting=csv.QUOTE_NONE)

        # Get headers from file and find the index of the gold column
        headers = next(reader)
        gold_index = headers.index("GOLD")
        w_diff_index = headers.index("word difference")

        # Read data rows
        for row in reader:
            # Get text titles
            text1 = row[0]
            text2 = row[1]

            # If text categories are the same, write a 1, otherwise 0
            new_row = row

            # Get each text as a string
            with open(texts_path + text1, 'r') as f:
                text1str = f.read().replace('\n', '')

            with open(texts_path + text2, 'r') as f:
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
            text1data = text1data.replace(text1_filename_without_ext, "")
            text2data = text2data.replace(text2_filename_without_ext, "")

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

            if print_individual_texts:
                print(new_row[0] + " (" + str(len(text1array)) + " words)" +
                      " vs " + new_row[1] + " (" + str(len(text2array)) +
                      " words)")

            # Add similarity depending mainly on the DC.coverage from
            # the MUC3 dataset, but also the word overlap
            if float(new_row[gold_index]) > 0.5:
                same += 1
                word_diffs["same"].append(int(row[w_diff_index]))
                ratios["same"].append(common_to_all)
                if print_individual_texts:
                    print("DC.coverage: same")
            else:
                different += 1
                word_diffs["diff"].append(int(row[w_diff_index]))
                ratios["diff"].append(common_to_all)
                if print_individual_texts:
                    print("DC.coverage: different")

            if print_individual_texts:
                print("Common words\t(" + str(len(common_items)) + "): " +
                      ', '.join([str(x) for x in sorted(common_items)]))
                print("All words\t\t(" + str(len(all_items)) + "): " +
                      ', '.join([str(x) for x in sorted(all_items)]))
                print("Ratio: " + str(common_to_all * 100) + "%")
                print()

            # Get text types of texts to count them
            for i in range(0, 2):
                # Count texts only once
                if row[i] in counted_texts:
                    continue
                else:
                    counted_texts.append(row[i])

                # Get text type
                text_type = get_text_type(texts_path, row[i])

                # Check which length
                if i == 0:
                    length = len(text1array)
                else:
                    length = len(text2array)

                # Add to existing array or create new
                if text_type in coverages:
                    coverages[text_type].append(length)
                else:
                    coverages[text_type] = []
                    coverages[text_type].append(length)

            # return
        print("Same categories:\t\t" + str(same))
        print("Different categories:\t" + str(different))
        print()
        print("Word count diffs (same): " + str(word_diffs["same"][:100]))
        print("\tavg: " + str(sum(word_diffs["same"])/len(word_diffs["same"])))
        print("Word count diffs (diff): " + str(word_diffs["diff"][:100]))
        print("\tavg: " + str(sum(word_diffs["diff"])/len(word_diffs["diff"])))
        print()
        print("Ratios (same): " + str(ratios["same"][:100]))
        print("\tavg: " + str(sum(ratios["same"])/len(ratios["same"])))
        print("Ratios (diff): " + str(ratios["diff"][:100]))
        print("\tavg: " + str(sum(ratios["diff"])/len(ratios["diff"])))
        print()

        # Print how many texts in each dc.coverage type, and their word counts
        print("Number of different DC.coverage values: " + str(len(coverages)))
        for text_type in coverages:
            print(text_type + " (" + str(len(coverages[text_type])) +
                  " texts) -> " + str(coverages[text_type]))


if __name__ == '__main__':
    main()
