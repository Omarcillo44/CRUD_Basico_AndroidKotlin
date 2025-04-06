package com.progmov.crud

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) : SQLiteOpenHelper(context, "miBase.db", null, 3) { // Versión 2

    override fun onCreate(db: SQLiteDatabase) {

        val query1 = """
        CREATE TABLE PRODUCTO (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            nombre TEXT NOT NULL,
            precio DOUBLE NOT NULL,
            descripcion TEXT,
            imagen TEXT
        );
    """.trimIndent()


        db.execSQL(query1)

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 3) { // Verificamos si la versión anterior es menor que la actual
            val alterQuery = "ALTER TABLE PRODUCTO ADD COLUMN imagen TEXT"

            val query2 = """

insert into PRODUCTO (nombre, precio, descripcion, imagen) values ('Baked Mac and Cheese', 5.99, 'Creamy macaroni and cheese baked to perfection.', 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAABGdBTUEAAK/INwWK6QAAABl0RVh0U29mdHdhcmUAQWRvYmUgSW1hZ2VSZWFkeXHJZTwAAAGrSURBVDjLvZPZLkNhFIV75zjvYm7VGFNCqoZUJ+roKUUpjRuqp61Wq0NKDMelGGqOxBSUIBKXWtWGZxAvobr8lWjChRgSF//dv9be+9trCwAI/vIE/26gXmviW5bqnb8yUK028qZjPfoPWEj4Ku5HBspgAz941IXZeze8N1bottSo8BTZviVWrEh546EO03EXpuJOdG63otJbjBKHkEp/Ml6yNYYzpuezWL4s5VMtT8acCMQcb5XL3eJE8VgBlR7BeMGW9Z4yT9y1CeyucuhdTGDxfftaBO7G4L+zg91UocxVmCiy51NpiP3n2treUPujL8xhOjYOzZYsQWANyRYlU4Y9Br6oHd5bDh0bCpSOixJiWx71YY09J5pM/WEbzFcDmHvwwBu2wnikg+lEj4mwBe5bC5h1OUqcwpdC60dxegRmR06TyjCF9G9z+qM2uCJmuMJmaNZaUrCSIi6X+jJIBBYtW5Cge7cd7sgoHDfDaAvKQGAlRZYc6ltJlMxX03UzlaRlBdQrzSCwksLRbOpHUSb7pcsnxCCwngvM2Rm/ugUCi84fycr4l2t8Bb6iqTxSCgNIAAAAAElFTkSuQmCC'),
('Portable Dog Water Bottle', 18.99, 'Convenient water bottle for pets on the go.', 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAABGdBTUEAAK/INwWK6QAAABl0RVh0U29mdHdhcmUAQWRvYmUgSW1hZ2VSZWFkeXHJZTwAAAHvSURBVBgZpcG/S9RxHMfx5+fuq10d/SJKQ+rUaLoWw60bIoIgaKhBpDksDBFbCooQiqAlURCp/gIjHIQWa4h+QDRYtFhDqOEdSqCSelbfe79f+UUEg35Jj0eQxP8Ip7sGLxaONt+eLSsrAe7IHXNDLlyOzHAJmWHubKuOl8bGipeH7rT0R81HmvuqMtlUTYaNyC6VrQ/oj8qxUuXZRTZqOVaKFZFMXDtTx0Z19E6SiCRHEiIhEBACb0ZH+Z3DTU2YG4kICTOjrXeCNfc760kUCgV+ZXFxEa8YicjccIm7HTkIAgVc4k/cHTcnEcmFmxMCyFkhCCKfzzM/P4fEOgEQAXA3EpG74W6IVYEVgrH3Y/xkdBh7PURl+hPp3bUc2nwMOEVUiWPMjM57M6zpbashUSgUSEwNDzBXfMXBs+1sasiz/G6E7PMnPD5e1RHJHHen59weQCTcnfXGH/bQ3HqBzMen8OwmW7bvoCGXY/SDuiJ3x12AEwCxqrHxAKVSCQh8L02QqW2Ak5dYE3XvJe2hPiqOTz64MRi3uBlujpnh7lgc4+64REtmJ+W3j8gOt/NteZoysPAljaUpBUn8zYvWuu7qrdmr+3ZVoig1xcLnChMzaYu/6nqQxL942br/ytJs6XzaQs7SKgoGToxUbv0ALswWDRrf9Y0AAAAASUVORK5CYII='),
('Balsamic Glaze', 4.79, 'Sweet and tangy balsamic reduction for drizzling.', 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAABGdBTUEAAK/INwWK6QAAABl0RVh0U29mdHdhcmUAQWRvYmUgSW1hZ2VSZWFkeXHJZTwAAAJgSURBVDjLY/j//z8DJZgsTV+9fAu+uHo8+GzvXECWAV+c3R//mTn9/ydLu4eka3ZyY/ts63T3k4Xt+4/GlqS74JONY+9Hc5tdH4wsmAmGgWv9xQKX2nMPnapOF4D4zxotum4sjfh/e2Pr/wtz3f5fnKodx/A7O3P97/TU37+Sk9ajG+Bcc/bJnI0X/hfM3/t/YlfJ/ef7yv9/uLTl///PT/+/v7Tm/8Eun08Mv9NSf//buun/z9jYvz8iIs0Qms/YO1ae/GBfdvTNtMm5y99dqv338/Gm/88PdP//dnnd/z8Pjvw/PzfjH8PPxIT1P2Nifv8Ij7j0PSjk3jdf/5Mr4poWOZUfe2hXdsT+yXYVj3eXa/79/Xbk/7e7Of/fnsr9f31x8v/j05P+Ruf1tqI4Gehftv6UybvP+CZ9+mDr/OpVnPGjd5cr///9fvz/1zvx/78/iv7/6VrV//szHV6c7XUxQAlEoH8lHKtOHbMvP7bUtuQQ5/F8pbLnp8uhmuP+f38Y9f/jler/d6Y6fbrZY2YCT4mudRcKgP59DvTvG6B/S0GC25olg6/tKP7599uh/9/vJ///8Sj2/wegS25Pdf52o8dCASUpO1WfeTB5zcX/QP++BAnMyZRNvrqt6v/F7RP+PzkZ+v/r/ej/by9U/L81xfHljV4LJYy84FBxvMCu9PADm6L94Lhe1en8//+nB/9vzAr5v69K6v/RGZ7/DzaZPL3UYa5DVGZqTtX+/fP4lP8/T078f7LV8f+qFJnPOyv19InOjaG2gt/b4zV+7+3w/L2n1+ng5nJdJXypFQA6mcPFnqo5OgAAAABJRU5ErkJggg=='),
 ('Home Delivery Food Journal', 14.99, 'Track your meals and nutrition with this handy food journal.', 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAABGdBTUEAAK/INwWK6QAAABl0RVh0U29mdHdhcmUAQWRvYmUgSW1hZ2VSZWFkeXHJZTwAAAJsSURBVDjLfZO9a5RBEIefvfcuucRUanGiJkYjiNoEET9II0gUsRC0sbASO1G0MVhIiCL+BdoJYmOnjYWJQsA0QRBRYkBUJESJ+dJLzvfe3Z3dsXiTS6LowDKzC7+H3ww7RlUBCJcuKrv3IkNDqAgaAtF74lIuHT+OPh9m3YtnhlVRXC5Wi3SVMIrkdxHUef6MFYAXVIRoLVEkB4k06uI/AIUGwHnUWujoIFhLtJZgLSHLMF1dxLSOOvcfB84PmLnxG837a7QcaoJisvRukDTDvRojOD/wT0DTyYlJpFDze8612aZ2QiigkmEkpVSfoFx4VDOHw+SfAKOq6MODB8A8cd3XK9Y1E9OfSPYL9RkqDkxCc9nQ+uHeFF5PlS6Pja6dQZA+2XW+YqWFkFYJLheqd8QsRaqzpPNVsvbTlbgY+v4aokbp8eu2E+uLuVAciCd6i4oj+jqyMEddWsGGnr9nEHV91AT1GVEcGjxRcnFwea5+GqP6Ypxsempj6C1OAHd7B+XO0hA1/0TiG9YbJziqn8eJUmPfuSs0d+6h/nZw6/uXQ7eGjpZ+LbWg88bXIIa11sWi3jEzNsrOI2cofxrGPDhL6+fHbOvYkKjRK7mD4EeK1fFTrriZGNwa6yqOWKtRrnTCiasrvfdvIolmW+6gHu8UX9+fKpmUQqkMMaIh3wlUMW2tpG+eQv8m7DXDj2uGhe8zhES/meVtdDe7LiAMuO7TlaxpM0EialPwKdV3wyzMTrNlg1AsTLI4I3z5ngSf6Y0GACDr23Eg1qQPH3uimvUEUDXzURj5KHybm/16IgmmIyT6VeHesUG5/Rt5eNFI8xvNdAAAAABJRU5ErkJggg=='),
('Electric Bike', 899.99, 'Eco-friendly electric bike with a 30-mile range.', 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAABGdBTUEAAK/INwWK6QAAABl0RVh0U29mdHdhcmUAQWRvYmUgSW1hZ2VSZWFkeXHJZTwAAANDSURBVDjLXZNLaFxlAIW/+5hn5pkXMWFSU0uN9mFaUlNIUqmkjVIFFRVEUczChVKyduNOETeC+FipEcXajS0VrcQWW2zEllSSUkNK0pjUJNPJZGYyd5I79/H/93dTS/EsD4ePs/k0pRT3ZnRiZBA4DhwAWgATqAKXVaA+/Wjwy5/v3Wv/AUYnRkxgDHj+6dxQJBtrwbIbsD0Q0kFQ4Hz+rJBC/iKEfPHzJ7/bvAsYnRgJAb/2Nu/qP5o7jOVEcPGYtzTCKkLJDlDCI4ZN3v2NydXJa8IXh7594XRVv/NkbH/Tzv6B9l7K0sPXNYTQqYswNmHKboQ1J8ZyNURn4lF62x7c67n+CQCjcuTWAPDByzufMq7LLfLUqQeCTSdB2Q1hOTC3UqO6tUUkW0JLz3GwuZvzM1e2f315bNroG9n34XB7327NiJHXBahW7pMJrhYM6o7OzMomQbRCZNsN3NQcxaCCWZHsbsrpVxdnG03gQC7VwZS9SjGq87DxAAGCpTUPx6mTblNYjePMBxvgKjIiwlzRZej+/fiu2GMCTXGzgSV7lTXXJhnfzoaXpFIJSLc4tGZtirIHy50momrU1kuUN+IkYkl8T2RMpZSmKfBcC0UOMyQ5+4ePFkuQnxfoKkV7S51kbRBrxcdhEt/7G5RCCKGZUgRly7M6mowMyegjLBdNHD9EOBoh2hrj5kIJ50qGwcJpjuZ/ILb+D1upGKUNiRRB1RS++HOxfKtjRzzHudsTZOI7CBs5fMdGGRnCiSwHF09xLDTJQ6+9RaRrF/Vr4/x16RxDVtHSfU98/P3sRbEt0YJpr1At/U4262IKl5CmiOgG3de/ofvwc0RvXkD76iXiC6fo6mxkoLCe0b84dnLcc/2fxmcu8lhHD2mjgWwiIB2WpMIOyZBLdrNAtK0Ljp+B98vw9gLm1ixxXzWbAL7rv3JhbuqSW/f2Du87xI01E6OqEFoNP9CxU43YUz/ScOZN3PptbKBmGUiD1bsyPfHJ40nhi5PCl0de7X/dRLahVBxN18hNnyA1fZHORh9TX6ZWFCwWDOk76h3t/zr3v9v3rBTyDSmDPYEMsnfq6jMl2+5ZWkzHPdUkDbWi4LPhcfHevzBSqkykNJyOAAAAAElFTkSuQmCC'),
('Multi-Purpose Plant Care Tool', 24.99, 'All-in-one tool for measuring soil moisture, light, and pH.', 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAABGdBTUEAAK/INwWK6QAAABl0RVh0U29mdHdhcmUAQWRvYmUgSW1hZ2VSZWFkeXHJZTwAAAIlSURBVDjLY/j//z8DJZiB6gY09895tGTDnv+tE+f+B/EnL1gHZofGpt4iyoCK5r5H63YcBmkAG5BRVPO/b/aK/0CDn+A1ICm75H/X9CX/azun/m+bNP+/iaUd2AAHN5//WSV1/wuqWsFiVvauyxWUVHkEhUWZwAYsWLOTo6i23aOpbzbYqYXVbf89/MP+u3gF/M8pa/gfm5b3PyKn6X/txGX/S1qmgOW4uXmq2NjZGcEGTJi7mmXKwvUPF63b9T+3vAmMqyeu+j9l+a7/fUu2/2qcvuF/be/8/9G5zf/DkwvBLmRmYXnAwMDADDYA6FxWkM3TFm/8n11a/x/k55Tc8v/RyTn/1bT0wDaCXAITj0svAOpi+AfErGAD0goqWf1CY35a2Dr99wqM+G9sYftfW9/4v6yC8lMRMXEDSRm5rWISUv+B/v4vKi75n5eP/z8jI+M7oAFM8ED0CYo6DAq4XYfP/F+15cD/7hnLQAG2AiSnqqmzorJlwv+1Ow6B5UAxwscveBglFtx8gv/kVzSDDQC66H98RuF/PWPzqyA5oM1XQTEAMiA1v+J/emH1fw5Orj8oBji6+/6HGQBTpKGt/1NRRZ1RQlr2HSjgYAaAwoKVle0/igHWjm7geAYlIJACUGDqGpn9B/qfX0lV4wrIAFAsweSAYYBqACiBGJhYggMP6Of/QJv/S8sq/AcGohTQv7c5ubj/A+MdFH2gGABj2mUmUjEAnjJojQ5aPHUAAAAASUVORK5CYII='),
('Grilled Veggie Burgers', 6.29, 'Delicious veggie burgers loaded with grilled vegetables.', 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAABGdBTUEAAK/INwWK6QAAABl0RVh0U29mdHdhcmUAQWRvYmUgSW1hZ2VSZWFkeXHJZTwAAAJFSURBVDjLlZLfS1NhGMcF/4Jz6YqkSTRCLDUZipDWSBdjpDFxIdEWLoJ+TJscmqGxtXZqadtqrZP9QoVqV1k0gtWl3rRLkc2z4zxn22lhSLdG9O05w4TC4bp44eWBz/f9vM/zVAGo6ooqjq6IAvNjBZ1BWavWKj1bFwLRMSmL/wNvBRDYbHpQQOttKdHiy8YaPdlEw1jWUnEAgax1qoBNkDsZzqFpXESdS9BWFLAJ4k+RwGSbdwW7L6cNOwYQyBwcW8F+VoiphdphgemdCaJn2rdhn+uv3jGAQMtR/yoI5NSCNeZNBz9zCCx4cfplz6veJ8bq7pC1cCwssx1BOdE+ISfaAnKs1S85SgEE8qQq7rqYEjUXUjBGQ9+5+RuIZ97g+ocRHA9N/jjxMA/bTBHtARkDzxRcef0VfXwehzxZx7Za9PIdd3wIwfk4LFMKBmeLNCG5ZNhyM8vZnivof1TAgVGRLfs3Y6hzY/jtR5x5UYTe8+kXjZdR69Qz3kYW5rCEfazgKAcXjvhaoXc3osk9joZrkZ+km6inSakjvvVuDTo2o/aNKWug90sM6Sb6SPVUpKSbVCd17qmCS/SlPc508q9V/vfQbiStvAJLNA/dqMCqtb0ugTXdlWAMSKCms2UDSNdgmJDBvf+GendG3UiGdLX0qjg0q+Aw1TTnU4ayAaTrMN2T4J1bgzmUQ93VZehcy+jmVuGc/oKB+zk0jwjQ2JcM2wZQdxnS5WudaXWdVd0k7QhLr7KawaV1Atdr7It8zdlF5jcS1qLOWBIaUgAAAABJRU5ErkJggg=='),
('Over-Ear Headphones', 59.99, 'Comfortable over-ear headphones with deep bass.', 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAABGdBTUEAAK/INwWK6QAAABl0RVh0U29mdHdhcmUAQWRvYmUgSW1hZ2VSZWFkeXHJZTwAAAKZSURBVDjLfVNLSFRhFP7uYx44XbyjFommzaKVEkVWRoFY9IYKLGgRLQpqKbhxEYSLiAipcVMLxXalWYuKCIVkiIQ2jaORUiRa+Jg0nNHJx/W+Oue/MwP2+i/nP/+995zv+85//l9yXRf/G/F4/IFlWRdM01TJg7ywtbU19l3SvwCGhoYKKaFD1/WzgUABDEqC40BEU044rKO39xXUvyUnEoljnFxSUlIWCoUwPjEFZnccF47rEJALm8AMw4A6Ojpq2LbtJxNBOZnsU6nU75IxP58iIAeHjxwX31ROLi+vQDq9INhdflxW6QrjF+JEUbgQL54/Q1X1TsHO/4QCRuXk5o44hUmQJJq9SXi2zOIibl/ZLRgH4+/AaktPnvYUsFRm5cBwUTFkWYZEJmeN1wzgOh5j9fYasXazeyCzAnonABmKoqwzOet58OYx45w2js50VJQlFJjZ9kiyBEVV8+zCcwnkvc55jJ2f2rGUsYQKAcATbwiXwIyRTQWifm9ImF6wxGp1oBs1g+2o7Z+FpRdjyeygrkgegJytk23s85/nojYTg//LR9RdakIgUoWV4T6MvH2NslTYA/D5VNy6vEO0xzQtLC8bQrLf76MyXCy0NmPbmUYEx2LAmxsoKNSxtaISk2MJqFTXnVisvy53UMgXaZoW4e4kk0mD1AVqf8wguDkCnGjKq1JbSqEZP7HuLrS1tTUSyKk9e/cd1DQdT588WqGeP9w/fO9ifcM5X+jbSxgrSSxTbGZRQWI8aOUBotHoRmrZbH39IWzQNDrKNlRFJtC7Sw0zj1v9WujalmJLVeVJZOYsTHxXbHPVvZ6/TCR9nux9T0/3rtz5z96LkQNdUy0D5ytWP3ydvqrYUqWtyFN0lO4f7bNu/gJT+aqduOCVCAAAAABJRU5ErkJggg=='),
('Eco-Friendly Beeswax Wraps', 14.99, 'Reusable wraps for food storage, replacing plastic wraps.', 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAABGdBTUEAAK/INwWK6QAAABl0RVh0U29mdHdhcmUAQWRvYmUgSW1hZ2VSZWFkeXHJZTwAAAK5SURBVDjLjZBbSNNxHMVXDz3UQ0892AVSs4Q0shQRA5uzCFGL5Rxqc1u5mZpNnWNN533eaDr976blZeFllc7mMLMl6IOGqdOpS4vAPaRbb6FiQjpPm5QahvmFw5cfP87nezgkAKS9JI4+zp5Wey3Ot57AnMZ9rYnn0RAV6HHoz/+eZl74SYq12d2x0OaGnapL9azeF6CBeYxY6PSHrZeDH8OVsOmCsaA9BYva8/u+AKroo5V2cy8Wh1RYMz/D8nsV5id60F/sZ90XgBoew51pydxYmuyAY7YTKxY97AMEihKu6v4J4JK92Ep26CLBIEPFoqwl033HCGHqT7uOj69dhbAbcjFY+wAXOOd7AgQ+R/4CMIPPUJTsMEd1PBk71SjjQV4nQYUiF/lSAbo+tqCkvwi+eec0F/lnD28BZPRLg0+Sb6Gz4B5m2sRo5dNAMCioTQpDk1kM9bgQVaYMlJsy0f6pAen6NAQlB6i2AAq6Z/uXfu2uwrTZZMjGH6HCJEDxaDpyRlMg+pACtoqFOVXU/wurKI6GYkKEfMN9pKvjwK26ibjSUFAl12B7GrENOHi5RqQQpe0qzIeWBW5dDArb2ei2KGG2GSF7lwK6zBcMoffrTfOB4OeJVL5peeAbUPpSh9xGLQSEBvUjqxAo5hFfcn29a7oaXTMEXCPt40DWl4TAVLdVknt4LY3G614xzDogmQE4I0DCABDTDdC1ADEEROT4ocdSj51jmFK6ACBNSfzXxzrk4L+yg9kLMPUbiNdugKZxIFINRModuPLwNB4b76LMyNo0l71lbSew1oTYOkoyEJs3DK4RYL9xJtADDANwx5WifA6xvCjclnqj0pi4edm1XW8nQEr63JwU1FNEzQ6ktej900dBzptyahpk8SRCsk3wvPHCKs9KLEgQehuchiVX7N+73NXfL+Zkqi9OGtlWAAAAAElFTkSuQmCC');
    """.trimIndent()

            db?.execSQL(alterQuery)
            db?.execSQL(query2)


        }
    }



    fun obtenerProductos(): List<Producto> {
        val lista = mutableListOf<Producto>()
        val baseDatos = writableDatabase
        val cursor = baseDatos.rawQuery("SELECT * FROM PRODUCTO", null)

        while (cursor.moveToNext()) {
            lista.add(
                Producto(
                    id = cursor.getInt(0),
                    nombre = cursor.getString(1),
                    precio = cursor.getDouble(2),
                    descripcion = cursor.getString(3),
                    imagen = cursor.getString(4) ?: "placeholder_base64" // Manejo de valores nulos
                )
            )
        }

        cursor.close()
        baseDatos.close()
        return lista
    }
}
