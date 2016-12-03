import os


def get_text_type(path, file):
    print(path + file)
    return "TODO"


def get_text_categories(path):
    # Dictionary to keep types for each text
    text_types = {}

    files = os.listdir(path)

    for file in files:
        text_type = get_text_type(path, file)
        text_types[file] = text_type

    return text_types


def main():
    path = "../texts/input/"

    text_categories = get_text_categories(path)

    # print(text_categories)

if __name__ == '__main__':
    main()
