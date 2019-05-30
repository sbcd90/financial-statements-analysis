import gensim
import numpy as np
np.random.seed(14)

import keras.backend as K
from keras.models import Sequential
from keras.layers import Embedding, Lambda, Dense
from keras.preprocessing.text import Tokenizer
from keras.preprocessing import sequence
from keras.utils import np_utils


class Training:

    def __init__(self, file_name):
        self.dim_embeddings = 100
        self.window_size = 2
        self.corpus = open(file_name).readlines()
        self.tokenizer = Tokenizer()
        self.w2v = None

    def initialize_vocab(self):
#        self.corpus = [sentence for sentence in self.corpus if sentence.count(" ") >= 2]
        self.corpus = self.corpus[0].split(". ")
        print(self.corpus)
        self.tokenizer.fit_on_texts(self.corpus)
        v = len(self.tokenizer.word_index) + 1
        print(v)
        nb_samples = sum(len(s) for s in self.corpus)
        print(self.tokenizer.word_index)
        print(list(enumerate(self.tokenizer.texts_to_sequences(self.corpus))))
        return v, nb_samples

    def generate_data(self, v, cbow, n):
        maxlen = self.window_size * 2

        for _ in range(n):
            loss = 0
            for wordstr in self.corpus:
                words = wordstr.split(" ")
                L = len(words)
                for index, word in enumerate(words):
                    contexts = []
                    labels = []
                    s = index - self.window_size
                    e = index + self.window_size + 1

                    if word.lower() in self.tokenizer.word_index:
                        contexts.append([self.tokenizer.word_index[words[i].lower()] for i in range(s, e) if 0 <= i < L and i != index and words[i].lower() in self.tokenizer.word_index])
                        labels.append(self.tokenizer.word_index[word.lower()])

                        x = sequence.pad_sequences(contexts, maxlen=maxlen)
                        y = np_utils.to_categorical(labels, v)
                        loss += cbow.train_on_batch(x, y)
            print(loss)

    def prepare_layers(self, v):
        cbow = Sequential()
        cbow.add(Embedding(input_dim=v, output_dim=self.dim_embeddings, input_length=self.window_size*2))
        cbow.add(Lambda(lambda x: K.mean(x, axis=1), output_shape=(self.dim_embeddings, )))
        cbow.add(Dense(v, activation="softmax"))
        cbow.compile(loss="categorical_crossentropy", optimizer="adadelta")
        return cbow

    def execute(self, n, file_to_dump, v, nb_samples):
        cbow = self.prepare_layers(v)
        self.generate_data(v, cbow, n)
        self.write_vectors(cbow, v, file_to_dump)

    def write_vectors(self, cbow, v, file_name):
        f = open(file_name, "w")
        f.write("{} {}\n".format(v - 1, self.dim_embeddings))
        vectors = cbow.get_weights()[0]
        for word, i in self.tokenizer.word_index.items():
            f.write("{} {}\n".format(word, " ".join(map(str, list(vectors[i, :])))))
        f.close()

    def load_vectors(self, file_to_read):
        self.w2v = gensim.models.KeyedVectors.load_word2vec_format("./" + file_to_read, binary=False, unicode_errors="ignore")
        return self.w2v
