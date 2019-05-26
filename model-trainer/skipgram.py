import gensim
import numpy as np
np.random.seed(14)

from keras.models import Model
from keras.layers import Input, Embedding, Dot, Reshape, Activation
from keras.preprocessing.text import Tokenizer
from keras.preprocessing.sequence import skipgrams


class SkipGram:

    def __init__(self, file_name):
        self.dim_embeddings = 128
        self.corpus = open(file_name).readlines()
        self.tokenizer = Tokenizer()
        self.w2v = None

    def initialize_vocab(self):
        self.corpus = [sentence for sentence in self.corpus if sentence.count(" ") >= 2]
        print(self.corpus)
        self.tokenizer.fit_on_texts(self.corpus)
        v = len(self.tokenizer.word_index) + 1
        print(self.tokenizer.word_index)
        print(list(enumerate(self.tokenizer.texts_to_sequences(self.corpus))))
        return v

    def prepare_layers(self, v):
        w_inputs = Input(shape=(1,), dtype="int32")
        w = Embedding(v, self.dim_embeddings)(w_inputs)

        c_inputs = Input(shape=(1,), dtype="int32")
        c = Embedding(v, self.dim_embeddings)(c_inputs)

        o = Dot(axes=2)([w, c])
        o = Reshape((1,), input_shape=(1, 1))(o)
        o = Activation("sigmoid")(o)

        skipGram = Model(inputs=[w_inputs, c_inputs], outputs=o)
        skipGram.summary()
        skipGram.compile(loss="binary_crossentropy", optimizer="adam")
        return skipGram

    def execute(self, n, file_to_dump):
        v = self.initialize_vocab()
        skipGram = self.prepare_layers(v)
        for _ in range(n):
            loss = 0
            for i, doc in enumerate(self.tokenizer.texts_to_sequences(self.corpus)):
                data, labels = skipgrams(sequence=doc, vocabulary_size=v, window_size=5, negative_samples=5.)
                x = [np.array(x) for x in zip(*data)]
                y = np.array(labels, dtype=np.int32)

                if x:
                    loss += skipGram.train_on_batch(x, y)

            print(loss)
        self.write_vectors(skipGram, v, file_to_dump)

    def write_vectors(self, skipGram, v, file_name):
        f = open(file_name, "w")
        f.write("{} {}\n".format(v - 1, self.dim_embeddings))
        vectors = skipGram.get_weights()[0]
        for word, i in self.tokenizer.word_index.items():
            f.write("{} {}\n".format(word, " ".join(map(str, list(vectors[i, :])))))
        f.close()

    def load_vectors(self, file_to_read):
        self.w2v = gensim.models.KeyedVectors.load_word2vec_format("./" + file_to_read, binary=False)
        return self.w2v
