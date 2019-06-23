from trainer.model_trainer import ModelTrainer

if __name__ == "__main__":
    model_trainer = ModelTrainer("Computer")
    model_trainer.calculate_ensemble_weights()