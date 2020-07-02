# If you call mobilesim.tools.print_perceived_objects
#   It will listen for an lcm message from perception 
#   and print out a list of the visible objects

import threading
import time
import select

import lcm
lcm = lcm.LCM()

from mobilesim.lcmtypes import object_data_list_t

# Data shared across threads
class SharedState:
    def __init__(self):
        self.exit = False
shared = SharedState()
    
# Thread to continually check for new lcm messages until told to exit
def lcm_handle_thread(shared_state):
    while not shared_state.exit:
        rfds, wfds, efds = select.select([lcm.fileno()], [], [], 0.2)
        if rfds:
            lcm.handle()

lcm_thread = threading.Thread(target=lcm_handle_thread, args=(shared,))
lcm_thread.start()

# Listen for a DETECTED_OBJECTS message and print each visible object
def handle_message(channel, data):
    obj_list = object_data_list_t.decode(data)
    for obj in obj_list.objects:
        if not obj.visible:
            continue
        props = dict( (cls.category, cls.name) for cls in obj.classifications )
        cat = props["category"]
        print(cat + "[" + str(obj.id) + "] " + " ".join(k + "=" + v for (k, v) in props.items() if k != "category"))
        if len(obj.contained_objects) > 0:
            print("   contains: " + str(obj.contained_objects))

    shared.exit = True

lcm.subscribe("DETECTED_OBJECTS", handle_message)

while not shared.exit:
    time.sleep(0.1)

lcm_thread.join()
