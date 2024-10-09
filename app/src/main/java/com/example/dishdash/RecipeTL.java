package com.example.dishdash;

    public class RecipeTL {
        private String name;
        private String detailsRecipe;
        private String imageUrl;
        private int imageclck;
        private String timeP;
        private int editbtn;
        private int deletebtn;

        // Constructor
        public RecipeTL(String name, String detailsRecipe, String imageUrl, int imageclck, String timeP, int editbtn, int deletebtn) {
            this.name = name;
            this.detailsRecipe = detailsRecipe;
            this.imageclck = imageclck;
            this.imageUrl = imageUrl;
            this.timeP = timeP;
            this.editbtn = editbtn;
            this.deletebtn = deletebtn;
        }

        // Getter and Setter for name
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDetailsRecipe() {
            return detailsRecipe;
        }

        public void setDetailsRecipe(String detailsRecipe) {
            this.detailsRecipe = detailsRecipe;
        }

        public int getImageclck() {
            return imageclck;
        }

        public void setImageclck(int imageclck) {
            this.imageclck = imageclck;
        }

        // Getter and Setter for imageUrl
        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getTimeP() {
            return timeP;
        }

        public void setTimeP(String timeP) {
            this.timeP = timeP;
        }

        public int getEditbtn() {
            return editbtn;
        }

        public void setEditbtn(int editbtn) {
            this.editbtn = editbtn;
        }

        public int getDeletebtn() {
            return deletebtn;
        }

        public void setDeletebtn(int deletebtn) {
            this.deletebtn = deletebtn;
        }
    }

