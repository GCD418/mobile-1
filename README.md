# Integrante
Gabriel Carranza Donaldson
# Combinación elegida
E50-2 -> Firestore + Realtime Database: lista colaborativa con contador
E50-5 -> Firestore + Notificación interna al agregar documento
# Decisiones
## E50-2
- Como es costumbre, unicamente la app para Android va a ser funcional.
- El acceso a la lista va a ser completamente global para todos los usuarios ya que no se tiene un servicio de autenticación (aún).
- Ya que por defecto no existe una transacción atómica entre RTDB y Firestore, se va a simular lógicamente un *commit*, sabiendo que esto no necesariamente garantiza la atomicidad en caso de fallas en la App a medio proceso (pero no hay tiempo de implementar una solución más robusta). Esto implica que si 2 clientes desmarcan simultáneamente una tarea, esta puede marcarse correctamente en Firestore pero incorrectamente en RTDB.
## E50-5
- 
