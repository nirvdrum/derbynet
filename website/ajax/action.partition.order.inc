<?php

// $_POST[partitionid_(ord)] = partitionid, (ord) = 1,2,3,...

// Reordering partitions should reorder the corresponding groups (by-partition) or subgroups (one-group).

require_once('inc/partitions.inc');

$ok = true;
if (!have_permission(SET_UP_PERMISSION)) {
  json_not_authorized();
  $ok = false;
}

if ($ok) {
  $stmt = $db->prepare('UPDATE Partitions SET sortorder = :sortorder WHERE partitionid = :partitionid');
  foreach ($_POST as $key => $val) {
    if (substr($key, 0, strlen('partitionid_')) == 'partitionid_') {
      $stmt->execute(array(':sortorder' => substr($key, strlen('partitionid_')),
                           ':partitionid' => $val));
    }
  }

  switch (group_formation_rule()) {
  case 'by-partition':
    read_single_value('UPDATE Classes SET sortorder = (SELECT Partitions.sortorder'
                      .'                             FROM '.inner_join('Partitions', 'Ranks',
                                                                       'Partitions.rankid = Ranks.rankid')
                      .'                             WHERE Ranks.classid = Classes.classid)');
    break;
  case 'one-group':
    read_single_value('UPDATE Ranks SET sortorder = (SELECT Partitions.sortorder FROM Partitions'
                      .'                             WHERE Partitions.rankid = Ranks.rankid)');
    break;
  }
  
  json_success();
}

?>