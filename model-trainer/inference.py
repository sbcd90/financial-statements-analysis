
class Inference:

    def __init__(self, word_vectors):
        self.word_vectors = word_vectors

    def get_most_positive_words(self, word):
        return "".join(list(map(lambda x: x[0], self.word_vectors.most_similar(positive=[word]))))

    def get_cosine_distance(self, word1, word2):
        try:
            word_vector1 = self.word_vectors.word_vec(word1)
            word_vector2 = self.word_vectors.word_vec(word2)

            return self.word_vectors.cosine_similarities(word_vector1, [word_vector2])[0]
        except:
            return 0.0
