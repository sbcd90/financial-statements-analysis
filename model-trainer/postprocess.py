import numpy as np
from sklearn.feature_extraction.text import TfidfVectorizer


class PostProcess:

    def __init__(self):
        self.vectorizer = TfidfVectorizer()

    def find_common_words(self, words_list, n):
        score_matrix = self.vectorizer.fit_transform(words_list)
        feature_list = np.array(self.vectorizer.get_feature_names())
        score_sorted = np.argsort(score_matrix.toarray()).flatten()[::-1]
        return feature_list[score_sorted][:n]


if __name__ == "__main__":
    tfIdf = PostProcess()
    print(tfIdf.find_common_words(5))
